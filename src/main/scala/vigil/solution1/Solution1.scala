package vigil.solution1

object Solution1 extends App {

  val localStorageManager = new LocalStorageManager("/input/", "/output/")
  val vip = new VigilInterviewProblem(localStorageManager)
  vip.solveTheProblem()

}
