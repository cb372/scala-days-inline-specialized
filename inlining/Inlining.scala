import scala.annotation._

object Inlining {

  @inline
  def inlineMe(a: Int, b: Int) = (a + b) * 2

  @noinline
  def dontInlineMe(a: Int, b: Int) = (a + b) * 2

  def foo = inlineMe(1, 2)

  @noinline
  def bar = dontInlineMe(1, 2)

  def main(args: Array[String]) = {
    for (i <- 1 to 1000000) {
      val result = bar
      if (result > 10)
        println(result)
    }
  }

}
