package benchmark.buffer

import scala.specialized
import scala.collection.mutable.BitSet
import scala.reflect.ClassTag
import org.openjdk.jmh.annotations._
import org.openjdk.jmh.infra.Blackhole
import java.util.concurrent.TimeUnit

final class AppendOnlyBuffer[A: ClassTag](initialCapacity: Int = 512) {

  private[this] var array = new Array[A](initialCapacity)
  private[this] var c = 0

  /**
   * Add the given value to the end of the buffer
   */
  def append(value: A): Unit = {
    expandIfNecessary()
    array(c) = value
    c += 1
  }

  /**
   * Apply the given function to all elements of the buffer,
   * in the order they were appended.
   */
  def foreach(f: A => Unit): Unit = {
    var i = 0
    while (i < c) {
      f(array(i))
      i += 1
    }
  }

  private[this] def expandIfNecessary(): Unit = {
    if (c == array.length) {
      val biggerArray = new Array[A](array.length * 2)
      System.arraycopy(array, 0, biggerArray, 0, array.length)
      array = biggerArray
    }
  }

}

final class SpecialAppendOnlyBuffer[@specialized A: ClassTag](initialCapacity: Int = 512) {

  private[this] var array = new Array[A](initialCapacity)
  private[this] var c = 0

  /**
   * Add the given value to the end of the buffer
   */
  def append(value: A): Unit = {
    expandIfNecessary()
    array(c) = value
    c += 1
  }

  /**
   * Apply the given function to all elements of the buffer,
   * in the order they were appended.
   */
  def foreach(f: Function1[A, Unit]): Unit = {
    var i = 0
    while (i < c) {
      f(array(i))
      i += 1
    }
  }

  private[this] def expandIfNecessary(): Unit = {
    if (c == array.length) {
      val biggerArray = new Array[A](array.length * 2)
      System.arraycopy(array, 0, biggerArray, 0, array.length)
      array = biggerArray
    }
  }

}


@State(Scope.Thread)
class AppendOnlyBufferBenchmark {

  val buffer = {
    val buf = new AppendOnlyBuffer[Int](1000000)
    var i = 0
    while (i < 1000000) {
      buf.append(i)
      i += 1
    }
    buf
  }
  val specialBuffer = {
    val buf = new SpecialAppendOnlyBuffer[Int](1000000)
    var i = 0
    while (i < 1000000) {
      buf.append(i)
      i += 1
    }
    buf
  }
  val stringBuffer = {
    val buf = new AppendOnlyBuffer[String](1000000)
    var i = 0
    while (i < 1000000) {
      buf.append(i.toString)
      i += 1
    }
    buf
  }
  val specialStringBuffer = {
    val buf = new SpecialAppendOnlyBuffer[String](1000000)
    var i = 0
    while (i < 1000000) {
      buf.append(i.toString)
      i += 1
    }
    buf
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.MICROSECONDS)
  def foreach(bh: Blackhole): Unit = {
    buffer.foreach((x: Int) => bh.consume(x))
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.MICROSECONDS)
  def specialized_foreach(bh: Blackhole): Unit = {
    specialBuffer.foreach((x: Int) => bh.consume(x))
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.MICROSECONDS)
  def stringForeach(bh: Blackhole): Unit = {
    stringBuffer.foreach((x: String) => bh.consume(x))
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.MICROSECONDS)
  def specialized_stringForeach(bh: Blackhole): Unit = {
    specialStringBuffer.foreach((x: String) => bh.consume(x))
  }


}
