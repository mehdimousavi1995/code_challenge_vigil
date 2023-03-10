package vigil.solution2

class FakeRedisClientImpl extends AbstractRedisClient {
  val map:collection.mutable.Map[Long, Long] = collection.mutable.Map.empty[Long, Long]

  override def set(key: Long, value: Long): Boolean = map.put(key, value).isDefined

  override def get(key: Long): Option[Long] = map.get(key)

  override def getKeys: List[Long] = map.keys.toList

  override def flushAllValues(): Unit = ()
}
