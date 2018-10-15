package gateways

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import javax.inject.Inject
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext

class MemoAPI @Inject()(ws: WSClient)(implicit ec: ExecutionContext) {

  private[this] val api = "http://localhost:9100"

  private[this] val mapper = new ObjectMapper
  mapper.registerModule(DefaultScalaModule)

  def search(mainText :Option[String]) = {
    val request = ws.url(api + "/memo").addQueryStringParameters(("mainText", mainText.getOrElse("")))
    val response = request.get()
    response.map(mapper.readValue(_.json.toString(), classof[]) )


    ???
  }
}
