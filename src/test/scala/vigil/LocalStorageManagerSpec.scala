package vigil

import org.scalatestplus.play.PlaySpec
import vigil.LocalStorageManager

import java.io.File
import scala.io.Source
import scala.util.Try

class LocalStorageManagerSpec extends PlaySpec {

  val inputPath = "/src/test/scala/vigil/testcasefiles/input1/"
  val outputPath = "/src/test/scala/vigil/testcasefiles/output1/"
  val sut = new LocalStorageManager(inputPath, outputPath)

  "getAllCsvAndTsvFilePath" should {
    "return nil when directory don't exists" in {
      val sut = new LocalStorageManager("/wrong-directory", outputPath)
      sut.getAllCsvAndTsvFilePath() mustBe None
    }

    "return all csv and tsv files" in {
      val result = sut.getAllCsvAndTsvFilePath()
      result.isDefined mustBe true
      result.get.size mustBe 2
      result.get.exists(_.endsWith("1.txt")) mustBe false
      result.get.exists(_.endsWith("2.pnt")) mustBe false
      result.get.exists(_.endsWith("3.pdf")) mustBe false
      result.get.exists(_.endsWith("4.csv")) mustBe true
      result.get.exists(_.endsWith("5.tsv")) mustBe true
    }
  }

  "readFromFile" should {
    "read all the line except header and return tuple for each line, it also should return 0 for empty string in either key or value" in {
      sut.readFromFile(os.pwd + inputPath + "4.csv") mustBe
        List(
          (1, 1), (2, 2), (0, 3), (4, 0), (5, 5)
        )
    }
  }

  "readAllKeyValueFromFile" should {
    "return all key values in csv and tsv files" in {
      sut.readAllKeyValueFromFile() mustBe List(
        (1, 1), (2, 2), (0, 3), (4, 0), (5, 5), (6, 6), (7, 0), (0, 8)
      )
    }
  }

  "writeToOutput" should {
    "write all the items in map to file successfully" in {
      val map: Map[Long, Long] = Map(
        1L -> 2L,
        3L -> 4L,
        5L -> 6L
      )
      sut.writeToOutput(map.toList)
      val oPath = os.pwd + outputPath + "output.tsv"
      val source = Source.fromFile(oPath)
      val output = source.getLines().toList
      source.close()
      output.size mustBe 3
      output.map { l =>
        val list = l.split("\t")
        map(list.head.toLong) mustBe list(1).toLong
      }
      Try {
        (new File(oPath)).delete()
      }
    }
  }

}
