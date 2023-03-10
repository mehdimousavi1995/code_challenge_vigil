package vigil.solution1

import vigil.LocalStorageManager

class VigilInterviewProblemSolution1(localStorageManager: LocalStorageManager) {

  def solveTheProblem(): Unit = {
    println("Starting to calculate the output")
    val result = localStorageManager.readAllKeyValueFromFile()
      .groupBy(_._1)
      .mapValues { vs =>
        vs.map(_._2).foldLeft(0L)(_ ^ _)
      }.toList
    localStorageManager.writeToOutput(result)
  }

}
