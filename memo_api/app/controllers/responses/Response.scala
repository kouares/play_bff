package controllers.responses

import services.results.Result._

object Response {
  case class MemoSearchResponse(errorMessage: Option[String], memoResRow: Seq[MemoResRow])

  case class MemoDeleteResponse(errorMessage: Option[String], id: Int)
}
