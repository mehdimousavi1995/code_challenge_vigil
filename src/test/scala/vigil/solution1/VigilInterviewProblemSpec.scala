package vigil.solution1

import org.scalatestplus.play.PlaySpec

import java.io.File
import scala.io.Source

class VigilInterviewProblemSpec extends PlaySpec {
  val dirName = "/src/test/scala/vigil/testcasefiles/input2/"
  val outputPath = "/output/"
  val localStorageManager = new LocalStorageManager(dirName, outputPath)

  val sut = new VigilInterviewProblem(localStorageManager)

  "solveTheProblem" should {
    "find the values that occur odd number of the times and append key and value in the tsv file" in {
      sut.solveTheProblem()

      val expectedOutputForAllTheFilesInInput2 = Map(
        1 -> 5,
        2 -> 5,
        6 -> 0,
        0 -> 4,
        12 -> 5,
        17 -> 1,
        85 -> 75
      ).map { case (k, v) => k.toLong -> v.toLong }

      val oPath = os.pwd + outputPath + "output.tsv"
      val source = Source.fromFile(oPath)
      val output = source.getLines().toList
      source.close()
      output.size mustBe 7
      output.map { l =>
        val list = l.split("\t")
        expectedOutputForAllTheFilesInInput2(list.head.toLong) mustBe list(1).toLong
      }
      (new File(oPath)).delete()
    }
  }



}