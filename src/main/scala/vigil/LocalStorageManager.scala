package vigil

import java.io.{File, PrintWriter}
import scala.annotation.tailrec
import scala.io.Source
import scala.util.Try

class LocalStorageManager(val inputDirName: String, outputDirName: String) {
  lazy val output = new File(os.pwd + outputDirName + "output.tsv")
  lazy val writer = new PrintWriter(output)

  def writeToOutput(key: Long, value: Long): Unit = {
    writer.write(s"$key\t$value\n")
  }
  def closeOutput = writer.close()


  def writeToOutput(result: List[(Long, Long)]): Unit = {
    val out = new File(os.pwd + outputDirName + "output.tsv")
    if (out.exists()) {
      out.delete()
    }
    val wr = new PrintWriter(out)
    writeRecursively(result)
    wr.close()

    @tailrec
    def writeRecursively(list: List[(Long, Long)]): Unit = list match {
      case (k, v) :: Nil => wr.write(s"$k\t$v")
      case (k, v) :: ls =>
        wr.write(s"$k\t$v\n")
        writeRecursively(ls)
      case Nil =>
    }
  }

  def readAllKeyValueFromFile(): List[(Long, Long)] = getAllCsvAndTsvFilePath.map { allFilePath =>
    allFilePath.flatMap(readFromFile)
  }.getOrElse(Nil)

  def readFromFile(filePath: String): List[(Long, Long)] = {
    val delimiter = if (filePath.contains("csv")) "," else "\t"
    val source = Source.fromFile(filePath)
    val dataWithoutHeader = source.getLines().toList.tail
    source.close()
    dataWithoutHeader.map { line =>
      val l = line.split(delimiter)
      val key: Long = Try(l.head.toLong).toOption.getOrElse(0L)
      val value: Long = Try(l(1).toLong).toOption.getOrElse(0L)
      key -> value
    }
  }

  def readFileLineByLineAndApplyTask(filePath: String, task: => String => Unit): Unit = {
    val source = Source.fromFile(filePath)
    for (line <- source.getLines()) {
      task(line)
    }
    source.close()
  }

  def getAllCsvAndTsvFilePath: Option[List[String]] = {
    val d = new File(os.pwd + inputDirName)
    if (d.exists && d.isDirectory) {
      val directories = d.listFiles.filter(f => f.isFile && (f.getName.endsWith(".csv") || f.getName.endsWith("tsv"))).toList
      Some(directories.map { f =>
        val path = os.pwd + inputDirName + f.getName
        println(path)
        path
      })
    } else {
      None
    }
  }

}
