package models
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.jdbc.MySQLProfile
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Label.schema ++ LabelMapping.schema ++ Memo.schema
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /**
    * Entity class storing rows of table Label
    *  @param name Database column name SqlType(VARCHAR), Length(100,true), Default(None)
    *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
    */
  case class LabelRow(name: Option[String] = None, id: Option[Int] = None)
  /** GetResult implicit for fetching LabelRow objects using plain SQL queries */
  implicit def GetResultLabelRow(implicit e0: GR[Option[String]], e1: GR[Option[Int]]): GR[LabelRow] = GR {
    prs =>
      import prs._
      val r = (<<?[Int], <<?[String])
      import r._
      LabelRow.tupled((_2, _1)) // putting AutoInc last
  }
  /** Table description of table label. Objects of this class serve as prototypes for rows in queries. */
  class Label(_tableTag: Tag) extends profile.api.Table[LabelRow](_tableTag, "label") {
    def * = (name, Rep.Some(id)) <> (LabelRow.tupled, LabelRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (name, Rep.Some(id)).shaped.<>({ r => import r._; _2.map(_ => LabelRow.tupled((_1, _2))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    /** Database column name SqlType(VARCHAR), Length(100,true), Default(None) */
    val name: Rep[Option[String]] = column[Option[String]]("name", O.Length(100, varying = true), O.Default(None))
    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
  }
  /** Collection-like TableQuery object for table Label */
  lazy val Label = new TableQuery(tag => new Label(tag))

  /**
    * Entity class storing rows of table LabelMapping
    *  @param memoId Database column memo_id SqlType(INT), Default(None)
    *  @param labelId Database column label_id SqlType(INT), Default(None)
    */
  case class LabelMappingRow(memoId: Option[Int] = None, labelId: Option[Int] = None)
  /** GetResult implicit for fetching LabelMappingRow objects using plain SQL queries */
  implicit def GetResultLabelMappingRow(implicit e0: GR[Option[Int]]): GR[LabelMappingRow] = GR {
    prs =>
      import prs._
      val r = (<<?[Int], <<?[Int])
      import r._
      LabelMappingRow.tupled((_1, _2)) // putting AutoInc last
  }
  /** Table description of table label_mapping. Objects of this class serve as prototypes for rows in queries. */
  class LabelMapping(_tableTag: Tag) extends profile.api.Table[LabelMappingRow](_tableTag, "label_mapping") {
    def * = (memoId, labelId) <> (LabelMappingRow.tupled, LabelMappingRow.unapply)

    /** Database column memo_id SqlType(INT), Default(None) */
    val memoId: Rep[Option[Int]] = column[Option[Int]]("memo_id", O.Default(None))
    /** Database column label_id SqlType(INT), Default(None) */
    val labelId: Rep[Option[Int]] = column[Option[Int]]("label_id", O.Default(None))

    /** Index over (labelId) (database name label_id) */
    val index1 = index("label_id", labelId)
    /** Index over (memoId) (database name memo_id) */
    val index2 = index("memo_id", memoId)
  }
  /** Collection-like TableQuery object for table LabelMapping */
  lazy val LabelMapping = new TableQuery(tag => new LabelMapping(tag))

  /**
    * Entity class storing rows of table Memo
    *  @param title Database column title SqlType(VARCHAR), Length(200,true)
    *  @param mainText Database column main_text SqlType(VARCHAR), Length(10000,true), Default(None)
    *  @param upadtedAt Database column upadted_at SqlType(DATETIME), Default(None)
    *  @param createdAt Database column created_at SqlType(DATETIME)
    *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
    */
  case class MemoRow(title: String, mainText: Option[String] = None, upadtedAt: Option[java.sql.Timestamp] = None, createdAt: java.sql.Timestamp, id: Option[Int] = None)
  /** GetResult implicit for fetching MemoRow objects using plain SQL queries */
  implicit def GetResultMemoRow(implicit e0: GR[String], e1: GR[Option[String]], e2: GR[Option[java.sql.Timestamp]], e3: GR[java.sql.Timestamp], e4: GR[Option[Int]]): GR[MemoRow] = GR {
    prs =>
      import prs._
      val r = (<<?[Int], <<[String], <<?[String], <<?[java.sql.Timestamp], <<[java.sql.Timestamp])
      import r._
      MemoRow.tupled((_2, _3, _4, _5, _1)) // putting AutoInc last
  }
  /** Table description of table memo. Objects of this class serve as prototypes for rows in queries. */
  class Memo(_tableTag: Tag) extends profile.api.Table[MemoRow](_tableTag, "memo") {
    def * = (title, mainText, upadtedAt, createdAt, Rep.Some(id)) <> (MemoRow.tupled, MemoRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(title), mainText, upadtedAt, Rep.Some(createdAt), Rep.Some(id)).shaped.<>({ r => import r._; _1.map(_ => MemoRow.tupled((_1.get, _2, _3, _4.get, _5))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    /** Database column title SqlType(VARCHAR), Length(200,true) */
    val title: Rep[String] = column[String]("title", O.Length(200, varying = true))
    /** Database column main_text SqlType(VARCHAR), Length(10000,true), Default(None) */
    val mainText: Rep[Option[String]] = column[Option[String]]("main_text", O.Length(10000, varying = true), O.Default(None))
    /** Database column upadted_at SqlType(DATETIME), Default(None) */
    val upadtedAt: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("upadted_at", O.Default(None))
    /** Database column created_at SqlType(DATETIME) */
    val createdAt: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("created_at")
    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
  }
  /** Collection-like TableQuery object for table Memo */
  lazy val Memo = new TableQuery(tag => new Memo(tag))
}
