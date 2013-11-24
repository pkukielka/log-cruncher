import java.io.File
import org.slf4j.LoggerFactory
import scala.io.Source

case class Event(file: File, lineNumber: Int, lines: List[String])

object FileScanner {
  val logger = LoggerFactory.getLogger(FileScanner.getClass)
}

class FileScanner(logsDirectory: String) {
  import FileScanner._

  def process(parser: LogParser) {
    tree(new File(logsDirectory)).filter(f => f.isFile).foreach {
      file =>
        if (parser.switchLogFile(file)) {
          logger.info("Parsing log file " + file.getPath)

          var eventLines = List.empty[String]
          var lineNumber = 1

          for (line <- Source.fromFile(file, "iso-8859-1").getLines) {
            if (parser.isEventStart(line)) {
              eventLines = List.empty[String]
            }
            else if (parser.isEventEnd(line)) {
              parser.processEvent(Event(file, lineNumber, eventLines.reverse))
            }

            eventLines = line :: eventLines
            lineNumber += 1
          }
        }
        else {
          logger.info("Log file " + file.getPath + " already exists. Skipping.")
        }
    }
  }

  private def tree(root: File, skipHidden: Boolean = true): Stream[File] = {
    if (!root.exists || (skipHidden && root.isHidden)) {
      Stream.empty
    }
    else {
      root #:: (
        root.listFiles match {
          case null => Stream.empty
          case files => files.toStream.flatMap(tree(_, skipHidden))
        })
    }
  }
}

