package vigil.solution2

import vigil.LocalStorageManager

import scala.util.Try

class VigilInterviewProblemSolution2(localStorageManager: LocalStorageManager, redisClient: AbstractRedisClient) {

  def solveTheProblem(): Unit = {
    localStorageManager.getAllCsvAndTsvFilePath() foreach { files =>
      files.foreach { filePath =>
        val delimiter = if (filePath.contains("csv")) "," else "\t"
        localStorageManager.readFileLineByLineAndApplyTask(
          filePath = filePath,
          task = normalizeLineAndXorValuesForTheSameKeyInRedis(delimiter)
        )
      }
    }
    redisClient.getKeys.foreach { key =>
      redisClient.get(key).map { value =>
        localStorageManager.writeToOutput(key, value)
      }
    }
    localStorageManager.closeOutput
  }

  def normalizeLineAndXorValuesForTheSameKeyInRedis(delimiter: String)(line: String): Unit = {
    val l = line.split(delimiter)
    val key: Long = Try(l.head.toLong).toOption.getOrElse(0L)
    val value: Long = Try(l(1).toLong).toOption.getOrElse(0L)

    val accXorValue = redisClient.get(key).getOrElse(0L)
    redisClient.set(key, accXorValue ^ value)
    ()
  }

}
