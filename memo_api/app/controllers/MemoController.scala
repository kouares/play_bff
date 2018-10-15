package controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import javax.inject.Inject
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.MemoService
import services.results.Result._
import services.request.Requests._

import scala.concurrent.ExecutionContext

import controllers.responses.Response._

class MemoController @Inject() (service: MemoService, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) with APIBaseController {

  val mapper = new ObjectMapper
  mapper.registerModule(DefaultScalaModule)

  /**
    * mainTextで検索した結果をJsonで返す
    * @return 200 検索結果あり、リストを返す、404 検索結果なし、メッセージ、空のリストを返す
    */
  def get(mainText: Option[String]) = Action.async(implicit rs => service.search(mainText).map {
    case Right(result) => Ok(mapper.writeValueAsString(MemoSearchResponse(None, result.memoRows))).as(JSON)
    case Left(result) => result.error match {
      case Some(error) => error.status match {
        case 404 => NotFound(mapper.writeValueAsString(MemoSearchResponse(Some(error.message), result.memoRows))).as(JSON)
      }
      case None => throw new IllegalStateException(s"エラーをハンドリングできません result.error = ${result.error}")
    }
  })

  /**
    * Memoを作成する
    * @return 200 作成成功、リソースのURIを返す、409 作成失敗、メッセージを返す
    */
  def post() = executeService(classOf[MemoReq], service.create())

  /**
    * Memoを更新する
    * @return
    */
  def put(id: Int) = executeService(classOf[MemoReq], service.update(id))

  /**
    * Memoを削除する
    * Httpステータスでエラーコード
    * メッセージ、削除したidをボディに含める
    * @return
    */
  def delete(id: Int) = Action.async(implicit rs => service.delete(id).map {
    case Right(result) => Ok(mapper.writeValueAsString(MemoDeleteResponse(None, result.id))).as(JSON)
    case Left(result) => result.error match {
      case Some(error) => error.status match {
        case 404 => NotFound(mapper.writeValueAsString(MemoDeleteResponse(Some(error.message), id))).as(JSON)
      }
      case None => throw new IllegalStateException(s"エラーをハンドリングできません result.error = ${result.error}")
    }
  })
}
