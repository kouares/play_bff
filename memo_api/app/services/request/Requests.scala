package services.request

object Requests {

  case class MemoReq(id: Option[Int], title: String, mainText: Option[String], labels: Seq[String])
}

