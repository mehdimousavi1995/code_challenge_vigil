package vigil.solution2

import com.redis.RedisClient
import com.typesafe.config.ConfigFactory
import vigil.LocalStorageManager


object Solution2 extends App {
  private val config = ConfigFactory.load()
  val localStorageManager = new LocalStorageManager(config.getString("inputDir"), config.getString("outputDir"))
  val redisClient = new RedisClientImpl(new RedisClient(config.getString("redis.host"), config.getInt("redis.port")))
  redisClient.flushAllValues()
  val vips = new VigilInterviewProblemSolution2(localStorageManager, redisClient)
  vips.solveTheProblem()
}
