package benchmark.fft

import scala.math._
import scala.annotation._
import org.openjdk.jmh.annotations._
import java.util.concurrent.TimeUnit

final case class ComplexWithInline(r: Double, i: Double) {
  @inline def +(x: ComplexWithInline) = ComplexWithInline(r + x.r, i + x.i)
  @inline def -(x: ComplexWithInline) = ComplexWithInline(r - x.r, i - x.i)
  @inline def *(x: ComplexWithInline) = ComplexWithInline(r * x.r - i * x.i, r * x.i + i * x.r)
}

final case class ComplexWithNoInline(r: Double, i: Double) {
  @noinline def +(x: ComplexWithNoInline) = ComplexWithNoInline(r + x.r, i + x.i)
  @noinline def -(x: ComplexWithNoInline) = ComplexWithNoInline(r - x.r, i - x.i)
  @noinline def *(x: ComplexWithNoInline) = ComplexWithNoInline(r * x.r - i * x.i, r * x.i + i * x.r)
}

/**
 * This benchmark code calculates a Fast Fourier Transform
 * of random data using the Cooley-Turkey algorithm.
 *
 * Implementation based on 
 * http://devonbryant.github.io/blog/2013/03/03/numerical-computing-with-scala/
 */
@State(Scope.Thread)
class FFTBenchmark {

  val input: Seq[Double] = Seq.tabulate(32768)(_ => scala.util.Random.nextDouble)

  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.MICROSECONDS)
  def fftWithInline: Seq[ComplexWithInline] = {
    def filterByIndex[A](xs: Seq[A])(p: Int => Boolean) =
      xs.zipWithIndex.collect { case (x, i) if p(i) => x }

    def rec(data: Seq[ComplexWithInline]): Seq[ComplexWithInline] = {
      data.length match {
        case 0 => Nil
        case 1 => data
        case n => {
          val evens = rec(filterByIndex(data) { _ % 2 == 0 })
          val odds = rec(filterByIndex(data) { _ % 2 != 0 })
          val phase = for (i <- 0 to n / 2 - 1) yield {
            val p = -2.0 * Pi * i / n
            ComplexWithInline(cos(p), sin(p))
          }

          val ops = (odds, phase).zipped map { _ * _ }
          val one = (evens, ops).zipped map { _ + _ }
          val two = (evens, ops).zipped map { _ - _ }

          one ++ two
        }
      }
    }

    rec(input map { a => ComplexWithInline(a, 0.0) })
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.MICROSECONDS)
  def fftWithNoInline: Seq[ComplexWithNoInline] = {
    def filterByIndex[A](xs: Seq[A])(p: Int => Boolean) =
      xs.zipWithIndex.collect { case (x, i) if p(i) => x }

    def rec(data: Seq[ComplexWithNoInline]): Seq[ComplexWithNoInline] = {
      data.length match {
        case 0 => Nil
        case 1 => data
        case n => {
          val evens = rec(filterByIndex(data) { _ % 2 == 0 })
          val odds = rec(filterByIndex(data) { _ % 2 != 0 })
          val phase = for (i <- 0 to n / 2 - 1) yield {
            val p = -2.0 * Pi * i / n
            ComplexWithNoInline(cos(p), sin(p))
          }

          val ops = (odds, phase).zipped map { _ * _ }
          val one = (evens, ops).zipped map { _ + _ }
          val two = (evens, ops).zipped map { _ - _ }

          one ++ two
        }
      }
    }

    rec(input map { a => ComplexWithNoInline(a, 0.0) })
  }

}
