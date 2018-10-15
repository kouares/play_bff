package controllers

import controllers.parser.JsonParserGenerator
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

trait APIBaseController extends AbstractController with JsonParserGenerator {

  def executeService[T](t: Class[T], service: T => Future[Either[String, String]])(implicit ec: ExecutionContext): Action[Either[Throwable, T]] = {

    val bodyParser = generateBodyParser(t)

    Action.async(bodyParser) { implicit rs =>
      rs.body match {
        case Right(param) => service(param).map {
          case Right(value) => Ok(value).as(JSON)
          case Left(value)  => BadRequest(value).as(JSON)
        }
        case Left(_) => Future(BadRequest)
      }
    }
  }
}
