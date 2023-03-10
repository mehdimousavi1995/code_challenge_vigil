package vigil.solution1

import com.typesafe.config.ConfigFactory
import vigil.LocalStorageManager

object Solution1 extends App {

  private val config = ConfigFactory.load()
  val localStorageManager = new LocalStorageManager(config.getString("inputDir"), config.getString("outputDir"))

  val vip = new VigilInterviewProblemSolution1(localStorageManager)

  vip.solveTheProblem()
}
