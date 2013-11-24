import scala.slick.driver.H2Driver.simple._

object Events extends Table[(Int, Int, Int, String)]("events") {
  def id          = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def logFileId   = column[Int]("log_file_id")
  def lineNumber  = column[Int]("line_number")
  def text        = column[String]("text")
  def autoInc     = logFileId ~ lineNumber ~ text returning id
  def *           = id ~ logFileId ~ lineNumber ~ text

  def logFiles = foreignKey("log_files_fk", logFileId, LogFiles)(_.id)
}

object LogFiles extends Table[(Int, String)]("log_files") {
  def id          = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def path        = column[String]("path")
  def autoInc     = path returning id
  def *           = id ~ path
}