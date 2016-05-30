

object FurtherOpt {

  final class A(x: Int) {
    @inline
    def addOne = x + 1
  }

  def two: Int = {
    val a = new A(1)
    a.addOne
  }

}
