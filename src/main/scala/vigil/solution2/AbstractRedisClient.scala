package vigil.solution2

import com.redis._

import scala.util.Try


trait AbstractRedisClient {
  def set(key: Long, value: Long): Boolean

  def get(key: Long): Option[Long]

  def getKeys: List[Long]

  def flushAllValues(): Unit
}
