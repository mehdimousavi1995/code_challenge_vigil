package vigil.solution3

import org.scalatestplus.play.PlaySpec
import vigil.LocalStorageManager

import java.io.File
import scala.io.Source
import scala.reflect.io.Directory
import scala.util.Try

class VigilInterviewProblemSolution3Spec extends PlaySpec {
  val dirName = "/src/test/scala/vigil/testcasefiles/input2/"
  val outputPath = "/output/output.tsv/"
  val localStorageManager = new LocalStorageManager(dirName, outputPath)

  val sut = new VigilInterviewProblemSolution3(localStorageManager)

  "solveTheProblem" should {
    "find the values that occur odd number of the times and append key and value in the tsv file" in {
//      Thread.sleep(1000)
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

      val out = localStorageManager.getAllCsvAndTsvFilePath(outputPath).map(_.head).get
      val source = Source.fromFile(out)
      val output = source.getLines().toList
      source.close()
      output.size mustBe 7
      output.map { l =>
        val list = l.split("\t")
        expectedOutputForAllTheFilesInInput2(list.head.toLong) mustBe list(1).toLong
      }
     Try {
       val dir = new Directory(new File(os.pwd + outputPath))
       dir.deleteRecursively()
     }
    }
  }


}
