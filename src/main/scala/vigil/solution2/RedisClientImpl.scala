package vigil.solution2

import com.redis.{RedisClient, serialization}
import com.redis.serialization.Parse

import scala.util.Try


class RedisClientImpl(redis: RedisClient) extends AbstractRedisClient {

  import serialization._
  import Parse.Implicits._

  private val prefix = "vigil_code_challenge_key_prefix_"

  private def uniqueKey(key: Long): String = s"$prefix$key"

  override def set(key: Long, value: Long): Boolean = redis.set(uniqueKey(key), value)

  override def get(key: Long): Option[Long] = redis.get[Long](uniqueKey(key))

  def getKeys: List[Long] = {
    var list = List.empty[Long]
    redis.keys[String](s"${prefix}*").getOrElse(Nil).foreach {
      case Some(key) => Try(key.substring(prefix.length).toLong).toOption.foreach(k => list = k :: list)
    }
    list
  }

  override def flushAllValues(): Unit = redis.flushall
}
