package services.response

import models.Tables._

object Responses {

  case class Error(status: Int, message: String)

  case class MemoSearchRes(error: Option[Error], memoRows: Seq[MemoResRow])
  case class MemoResRow(memoRow: MemoRow, labels: Seq[LabelRow])

  case class MemoCreateRes(error: Option[Error], id: Int)

  case class MemoUpdateRes(error: Option[Error], id: Int)

  case class MemoDeleteRes(error: Option[Error], id: Int)
}
