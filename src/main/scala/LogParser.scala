import java.io.File
import scala.slick.driver.H2Driver.simple._

abstract class LogParser(implicit session: Session) {
  var currentLogFileId: Option[Int] = None

  def isEventStart(line: String): Boolean

  def isEventEnd(line: String): Boolean

  def parseEvent(event: Event, eventId: Int): Unit

  final def switchLogFile(file: File) = {
    if (Query(LogFiles).filter(_.path === file.getPath).list.isEmpty) {
      currentLogFileId = Some(LogFiles.autoInc.insert(file.getPath))
      true
    }
    else {
      false
    }
  }

  final def processEvent(event: Event) {
    val eventId = Events.autoInc.insert((currentLogFileId.get, event.lineNumber, event.lines.mkString("\n")))
    parseEvent(event, eventId)
  }
}
