package vigil.solution3

import com.typesafe.config.ConfigFactory
import vigil.LocalStorageManager

object Solution3 extends App {

  private val config = ConfigFactory.load()
  val localStorageManager = new LocalStorageManager(config.getString("inputDir"), config.getString("outputDir"))

  val vip = new VigilInterviewProblemSolution3(localStorageManager)
  vip.solveTheProblem()


}
