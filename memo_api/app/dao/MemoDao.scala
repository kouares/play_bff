

package dao

import java.sql.Timestamp

import services.request.Requests.MemoReq

import scala.concurrent.{ExecutionContext, Future}
import javax.inject.{Inject, Singleton}
import models.Tables._
import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._

//case class MemoItem(id: Int, title: String, mainText: String)
//case class MemoInfo(id: Int, title: String, mainText: String, tags: Seq[TagRow])

trait MemoDao extends HasDatabaseConfigProvider[JdbcProfile] {

  protected val logger = Logger(classOf[MemoDao])

  def currentTime = new Timestamp(System.currentTimeMillis())

  def search(mainText: Option[String]): Future[Seq[(MemoRow, Seq[LabelRow])]]

  //  def findById(id: Int): Future[Option[MemoInfo]]

  def create(memo: MemoReq): Future[Int]

  def update(id: Int, memo: MemoReq): Future[Option[Int]]

  def delete(id: Int): Future[Int]
}

@Singleton class MemoDaoImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends MemoDao {
  def search(mainText: Option[String]): Future[Seq[(MemoRow, Seq[LabelRow])]] = {
    // クエリを作成
    val query = for {
      // 検索条件が指定されていれば条件を使って検索、指定されていないなら全件取得
      memos <- mainText.fold(Memo.sortBy(_.id))(mainText => Memo.filter(_.mainText like s"%$mainText%").sortBy(_.id)).result

      // 取得したメモに紐づくタグを取得する
      tags <- Memo.filter(_.id.inSetBind(memos.map(_.id.getOrElse(0))))
        .join(LabelMapping).on((m, t) => m.id === t.memoId)
        .join(Label).on((mt, tm) => mt._2.labelId === tm.id)
        .map(result => (result._1._1.id, result._2)).result
    } yield memos.map(r => (r, tags.filter(_._1 == r.id.getOrElse(0)).map(_._2)))

    db.run(query)
  }

  def create(memo: MemoReq): Future[Int] = {
    val transaction = (for {
      memoId <- Memo returning Memo.map(_.id) += MemoRow(memo.title, memo.mainText, None, new Timestamp(System.currentTimeMillis()))

      _ <- DBIO.sequence(memo.labels.map(label =>
        Label.filter(_.name === label).exists.result.flatMap {
          case true  => DBIO.successful(0)
          case false => (Label returning Label.map(_.id) += LabelRow(Some(label))).map(DBIO.successful).flatten
        }
      ))

      _ <- Label.filter(_.name.inSetBind(memo.labels)).result.flatMap(labels => LabelMapping ++= labels.map(label => LabelMappingRow(Some(memoId), label.id)))
    } yield memoId).transactionally

    db.run(transaction)
  }

  def update(id: Int, memo: MemoReq): Future[Option[Int]] = {

    val transaction = (for {
      updatedId <- Memo.filter(_.id === id)
        .map(result => (result.title, result.mainText, result.upadtedAt))
        .update((memo.title, memo.mainText, Some(currentTime))).map {
          case updated if updated == 1 => Some(id)
          case _                       => None
        }

      _ <- DBIO.sequence(memo.labels.filter(!_.endsWith("-remove")).map(label =>
        Label.filter(_.name === label)
          .exists.result.flatMap {
            case true  => DBIO.successful(0)
            case false => (Label returning Label.map(_.id) += LabelRow(Some(label))).map(DBIO.successful).flatten
          }))

      _ <- DBIO.sequence(memo.labels.map(label => if (label.endsWith("-remove")) {
        Label.filter(_.name === label.replace("-remove", "")).result.headOption.flatMap {
          case Some(labelRow) => LabelMapping.filter(labelMap => labelMap.memoId === updatedId && labelMap.labelId === labelRow.id).delete
          case None           => DBIO.successful(0)
        }
      } else {
        Label.filter(_.name === label).result.headOption.flatMap(_.map(labelRow =>
          LabelMapping.filter(labelMap => labelMap.memoId === updatedId && labelMap.labelId === labelRow.id).exists.result.flatMap {
            case true  => DBIO.successful(0)
            case false => LabelMapping += LabelMappingRow(updatedId, labelRow.id)
          }).getOrElse(DBIO.successful(0)))
      }))
    } yield updatedId).transactionally

    db.run(transaction)
  }

  def delete(id: Int) = {
    db.run(LabelMapping.filter(_.memoId === id).delete.flatMap(_ => Memo.filter(_.id === id).delete))
  }
}

//
//@Singleton class MemoDaoImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends MemoDao {
//
//  /**
//    * 検索
//    */
//  def search(mainText: Option[String]) = {
//    // クエリを作成
//    val query = for {
//      // 検索条件が指定されていれば条件を使って検索、指定されていないなら全件取得
//      memos <- mainText.fold(Memo.sortBy(_.id))(mainText => Memo.filter(_.mainText like s"%${mainText}%").sortBy(_.id)).result
//        .map(_.map(memo => MemoItem(memo.id.getOrElse(-1), memo.title, memo.mainText.getOrElse(""))))
//
//      // 取得したメモに紐づくタグを取得する
//      tags <- Memo.filter(_.id.inSetBind(memos.map(_.id)))
//        .join(TagMapping).on((m, t) => m.id === t.memoId)
//        .join(TagMst).on((mt, tm) => mt._2.tagId === tm.id)
//        .map(result => (result._1._1.id, result._2)).result
//    } yield memos.map(memo =>
//      MemoInfo(memo.id, memo.title, memo.mainText, tags.filter(tagMap => tagMap._1 == memo.id).map(_._2)))
//
//    db.run(query)
//  }
//
//  /**
//    * id指定検索
//    */
//  def findById(id: Int) = {
//    val query = for {
//      // メモを検索
//      memo <- Memo.findBy(_.id).applied(id).result.headOption
//        .map(_.map(memo => MemoItem(memo.id.getOrElse(-1), memo.title, memo.mainText.getOrElse(""))))
//
//      // メモに付いているタグを検索
//      tags <- TagMapping.findBy(_.memoId).applied(memo.map(_.id))
//        .join(TagMst).on((tMap, tMst) => tMap.tagId === tMst.id)
//        .map(result => (result._2)).result
//    } yield memo.map(memo => MemoInfo(memo.id, memo.title, memo.mainText, tags))
//
//    db.run(query)
//  }
//
//  /**
//    * 登録
//    */
//  def create(form: MemoForm) = {
//    // トランザクションを作成
//    val transaction = (for {
//      // メモを登録してidを取得
//      memoId <- Memo returning Memo.map(_.id) += MemoRow(form.title, form.mainText, None, new Timestamp(System.currentTimeMillis()))
//
//      // ひも付けられたタグがマスタに存在しない場合、タグを登録
//      _ <- DBIO.sequence(form.tags.map(tag =>
//        TagMst.filter(_.name === tag).exists.result.flatMap {
//          case true => DBIO.successful(0)
//          case false => (TagMst returning TagMst.map(_.id) += TagMstRow(Some(tag))).map(DBIO.successful(_)).flatten
//        }))
//
//      // メモとタグのマッピングを登録
//      _ <- TagMst.filter(_.name.inSetBind(form.tags)).result.flatMap(tags =>
//        TagMapping ++= tags.map(tag => TagMappingRow(Some(memoId), tag.id)))
//    } yield memoId).transactionally
//
//    db.run(transaction)
//  }
//
//  /**
//    * 更新
//    */
//  def update(id: Int, form: MemoForm) = {
//    // トランザクションを作成
//    val transaction = (for {
//      // メモをアップデートして更新したメモのidを取得
//      updatedMemoId <- Memo.filter(memo => memo.id === id)
//        .map(result => (result.title, result.mainText, result.upadtedAt))
//        .update((form.title, form.mainText, Some(new Timestamp(System.currentTimeMillis())))).map(_ match {
//        case updated if updated == 1 => Some(id)
//        case _ => None
//      })
//
//      // メモとタグのマッピング前にタグがマスタに存在していない場合はタグを登録
//      _ <- DBIO.sequence(form.tags.filter(!_.endsWith("-remove")).map(tag =>
//        TagMst.filter(_.name === tag)
//          .exists.result.flatMap {
//          case true => DBIO.successful(0)
//          case false => (TagMst returning TagMst.map(_.id) += TagMstRow(Some(tag))).map(DBIO.successful(_)).flatten
//        }))
//
//      // メモとタグのマッピングの削除と登録
//      _ <- DBIO.sequence(form.tags.map(tag => tag.endsWith("-remove") match {
//        case true => TagMst.filter(_.name === tag.replace("-remove", "")).result.headOption.flatMap {
//          case Some(tagMstRow) => TagMapping.filter(tagMap => tagMap.memoId === updatedMemoId && tagMap.tagId === tagMstRow.id).delete
//          case None => DBIO.successful(0)
//        }
//        case false => TagMst.filter(_.name === tag).result.headOption.flatMap(_.map(tagMstRow =>
//          TagMapping.filter(tagMap => tagMap.memoId === updatedMemoId && tagMap.tagId === tagMstRow.id).exists.result.flatMap {
//            case true => DBIO.successful(0)
//            case false => TagMapping += TagMappingRow(updatedMemoId, tagMstRow.id)
//          }).getOrElse(DBIO.successful(0)))
//      }))
//    } yield updatedMemoId).transactionally
//
//    db.run(transaction)
//  }
//
//  /**
//    * 削除
//    */
//  def delete(id: Int) = {
//    // メモとタグのひも付きを削除
//    db.run(TagMapping.filter(_.memoId === id).delete.flatMap(_ => Memo.filter(_.id === id).delete))
//  }
//}
