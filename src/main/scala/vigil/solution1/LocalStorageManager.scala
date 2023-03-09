package vigil.solution1

import java.io.{File, PrintWriter}
import scala.annotation.tailrec
import scala.io.Source
import scala.util.Try

class LocalStorageManager(val inputDirName: String, outputDirName: String) {


  def writeToOutput(result: List[(Long, Long)]): Unit = {
    val output = new File(os.pwd + outputDirName + "output.tsv")
    if (output.exists()) {
      output.delete()
    }
    val writer = new PrintWriter(output)
    writeRecursively(result)
    writer.close()

    @tailrec
    def writeRecursively(list: List[(Long, Long)]): Unit = list match {
      case (k, v) :: Nil => writer.write(s"$k\t$v")
      case (k, v) :: ls =>
        writer.write(s"$k\t$v\n")
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
