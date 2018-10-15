package services

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import dao.MemoDao
import javax.inject.Inject
import services.request.Requests.MemoReq
import services.results.Result._

import scala.concurrent.{ExecutionContext, Future}

trait MemoService {
  def search(mainText: Option[String]): Future[Either[MemoSearch, MemoSearch]]
  def create()(request: MemoReq): Future[Either[String, String]]
  def update(id: Int)(request: MemoReq): Future[Either[String, String]]
  def delete(id: Int): Future[Either[MemoDelete, MemoDelete]]
}

class MemoServiceImpl @Inject() (dao: MemoDao)(implicit ex: ExecutionContext) extends MemoService {

  val mapper = new ObjectMapper
  mapper.registerModule(DefaultScalaModule)

  def search(mainText: Option[String]): Future[Either[MemoSearch, MemoSearch]] = {
    dao.search(mainText).map(_.map(r => MemoResRow(r._1, r._2))).map(r =>
      if (r.nonEmpty) Right(MemoSearch(None, r))
      else Left(MemoSearch(Some(Error(404, "not found")), r)))
  }

  def create()(request: MemoReq): Future[Either[String, String]] = {
    dao.create(request).map {
      case count if count == 1 => Right(mapper.writeValueAsString(MemoCreate(None, count)))
      case _@ errorCount       => Left(mapper.writeValueAsString(MemoCreate(Some(Error(409, s"failed to create. created count = $errorCount")), errorCount)))
    }
  }

  def update(id: Int)(request: MemoReq): Future[Either[String, String]] = {
    dao.update(id, request).map {
      case Some(count) if count == 1 => Right(mapper.writeValueAsString(MemoUpdate(None, id)))
      case Some(count)               => Left(mapper.writeValueAsString(MemoUpdate(Some(Error(409, s"failed to update. update count = $count")), id)))
      case None                      => Left(mapper.writeValueAsString(MemoUpdate(Some(Error(404, "target not found.")), id)))
    }
  }

  def delete(id: Int): Future[Either[MemoDelete, MemoDelete]] = {
    dao.delete(id).map {
      case 1 => Right(MemoDelete(None, id))
      case 0 => Left(MemoDelete(Some(Error(404, "target not found")), id))
    }
  }
}
