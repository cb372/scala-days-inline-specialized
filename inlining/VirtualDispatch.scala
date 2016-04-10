import scala.annotation._

object VirtualDispatch {

  abstract class A {
    @inline
    def name: String
  }

  class B1 extends A {
    def name: String = "B1"
  }

  class B2 extends A {
    def name: String = "B2"
  }

  def foo(a: A) = s"I got a ${a.name}"

  def main(args: Array[String]): Unit = {
    val b1 = new B1
    for (i <- 1 to 20000) {
      if (foo(b1) == "wut")
        println("wut")
    }
  }
}
