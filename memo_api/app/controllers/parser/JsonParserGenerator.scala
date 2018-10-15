package controllers.parser

import play.api.mvc._
import play.api.libs.streams._
import akka.util.ByteString
import akka.stream.scaladsl._

import scala.util.{Left, Right}
import scala.concurrent.{ExecutionContext, Future}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.annotation.JsonInclude

trait JsonParserGenerator {

  /** リクエストのBody部からJsonオブジェクトの取得に失敗した場合のエラー */
  val noJsonBodyError = Left(new Exception("リクエストBody部からJSONオブジェクトの取得に失敗"))

  /** Json Object Mapper */
  val Mapper = new ObjectMapper
  Mapper.registerModule(DefaultScalaModule)
  Mapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT)

  def generateBodyParser[T](t: Class[T])(implicit ec: ExecutionContext): BodyParser[Either[Throwable, T]] = {
    BodyParser { _ =>
      val flow = Flow[ByteString].map { jsonText =>
        try {
          Right(Mapper.readValue(jsonText.toArray, t))
        } catch {
          case e @ (_: JsonParseException | _: JsonMappingException) => Left(e)
          case e: Throwable => throw e
        }
      }

      val sink: Sink[ByteString, Future[Either[Throwable, T]]] = flow.toMat(Sink.fold(noJsonBodyError: Either[Throwable, T]) { (x, y) => y })(Keep.right)

      Accumulator(sink).map(Right.apply)
    }
  }
}
