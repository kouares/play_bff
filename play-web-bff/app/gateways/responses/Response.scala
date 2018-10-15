package gateways.responses

object Response {
  case class MemoSearchResponse(errorMessage: Option[String], memoResRow: Seq[MemoResRow])
}
