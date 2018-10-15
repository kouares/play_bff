package services.results

import models.Tables._

object Result {
  case class Error(status: Int, message: String)

  case class MemoSearch(error: Option[Error], memoRows: Seq[MemoResRow])
  case class MemoResRow(memoRow: MemoRow, labels: Seq[LabelRow])

  case class MemoCreate(error: Option[Error], id: Int)

  case class MemoUpdate(error: Option[Error], id: Int)

  case class MemoDelete(error: Option[Error], id: Int)
}
