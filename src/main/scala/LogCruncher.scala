import com.typesafe.config.ConfigFactory
import scala.slick.driver.H2Driver.simple._
import Database.threadLocalSession
import scala.slick.jdbc.meta.MTable

object LogCruncher extends App {
  val conf = ConfigFactory.load()
  val dbConf = conf.getConfig("database")

  Database.forURL(
    url       = dbConf.getString("url"),
    user      = if (dbConf.hasPath("user")) dbConf.getString("user") else null,
    password  = if (dbConf.hasPath("password")) dbConf.getString("password") else null,
    driver    = dbConf.getString("driver")
  ) withSession {

    if (MTable.getTables.list().isEmpty) {
      (LogFiles.ddl ++ Events.ddl).create
    }

    new FileScanner(conf.getString("logsDirectory")).process(new LogParser {
      override def isEventStart(line: String) = line.startsWith("===  ")

      override def isEventEnd(line: String) = line.isEmpty

      override def parseEvent(event: Event, eventId: Int): Unit = {}
    })
  }
}
