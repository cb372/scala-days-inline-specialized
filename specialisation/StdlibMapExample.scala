import scala.collection.mutable

object StdlibMapExample {

  def foo(): Unit = {
    val map = mutable.Map.empty[String, Int]
    map.put("key", 123)
  }

}
