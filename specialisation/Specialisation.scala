import scala.annotation._

class MySpecialMap[@specialized A] {
  def put(key: String, value: A): Unit = {}
  def get(key: String): Option[A] = None
}

class MyNotVerySpecialMap[A] {
  def put(key: String, value: A): Unit = {}
  def get(key: String): Option[A] = None
}

object Specialisation {

  def main(args: Array[String]) = {
    val x: Int = 123
    val hello: String = "hello"

    val map1 = new MySpecialMap[Int]
    val map2 = new MySpecialMap[String]
    map1.put("foo", x)
    map2.put("bar", hello)

    val map3 = new MyNotVerySpecialMap[Int]
    val map4 = new MyNotVerySpecialMap[String]
    map3.put("foo", x)
    map4.put("bar", hello)
  }

}
