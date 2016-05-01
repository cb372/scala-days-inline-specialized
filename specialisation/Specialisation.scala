import scala.annotation._

class MySpecialMap[@specialized A] {
  def put(key: String, value: A): Unit = {}
  def get(key: String): Option[A] = None
}

class MyNotVerySpecialMap[A] {
  def put(key: String, value: A): Unit = {}
  def get(key: String): Option[A] = None
}

object Test {

  def foo(): Unit = {
    val map1 = new MySpecialMap[Int]
    map1.put("key", 123)
  }

}
