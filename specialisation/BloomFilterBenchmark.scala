package benchmark.bloom

import scala.specialized
import scala.collection.mutable.BitSet
import org.openjdk.jmh.annotations._
import org.openjdk.jmh.infra.Blackhole
import java.util.concurrent.TimeUnit

/**
 * Typeclass for a provider of 2 hash functions, alpha and beta.
 * Each hash function takes a value of type `A` and return a positive integer.
 */
trait HashFunctions[A] {
  def alpha(value: A): Int 
  def beta(value: A): Int 
}

object HashFunctions {

  implicit val intHashFunctions = new HashFunctions[Int] {
    def alpha(value: Int) = Math.abs(value)
    def beta(value: Int) = Math.abs(value + 37)
  }

}

trait SpecializedHashFunctions[@specialized(Int) A] {
  def alpha(value: A): Int 
  def beta(value: A): Int 
}

object SpecializedHashFunctions {

  implicit val intHashFunctions = new SpecializedHashFunctions[Int] {
    def alpha(value: Int) = Math.abs(value)
    def beta(value: Int) = Math.abs(value + 37)
  }

}

/**
 * Bloom filter implementation using 'enhanced double hashing'.
 * See http://www.ccs.neu.edu/home/pete/pub/bloom-filters-verification.pdf for details.
 *
 * @param m the size of the bitset to use
 * @param k the number of bits to set for each element added
 */
final class BloomFilter[A](m: Int, k: Int)(implicit hashFunctions: HashFunctions[A]) {

  private val bits = new BitSet(m)

  /**
   * Mark the given value as being a member of the set
   */
  def add(value: A): Unit = {
    var x = hashFunctions.alpha(value) % m
    var y = hashFunctions.beta(value) % m
    bits += x
    for (i <- 1 until k) {
      x = (x + y) % m
      y = (y + i) % m
      bits += x
    }
  }

  /**
   * Check if the given value is a member of the set
   *
   * @return true if the value is *probably* a member of the set, false if the value is definitely not a member of the set
   */
  def query(value: A): Boolean = {
    var x = hashFunctions.alpha(value) % m
    var y = hashFunctions.beta(value) % m
    if (!bits(x)) return false
    for (i <- 1 until k) {
      x = (x + y) % m
      y = (y + i) % m
      if (!bits(x)) return false
    }
    true
  }

}

final class SpecializedBloomFilter[@specialized(Int) A](m: Int, k: Int)(implicit hashFunctions: SpecializedHashFunctions[A]) {

  private val bits = new BitSet(m)

  def add(value: A): Unit = {
    var x = hashFunctions.alpha(value) % m
    var y = hashFunctions.beta(value) % m
    bits += x
    for (i <- 1 until k) {
      x = (x + y) % m
      y = (y + i) % m
      bits += x
    }
  }

  def query(value: A): Boolean = {
    var x = hashFunctions.alpha(value) % m
    var y = hashFunctions.beta(value) % m
    if (!bits(x)) return false
    for (i <- 1 until k) {
      x = (x + y) % m
      y = (y + i) % m
      if (!bits(x)) return false
    }
    true
  }

}


@State(Scope.Thread)
class BloomFilterBenchmark {

  val bloomFilter = new BloomFilter[Int](m = 65536, k = 10)
  val specializedBloomFilter = new SpecializedBloomFilter[Int](m = 65536, k = 10)

  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.MICROSECONDS)
  def unspecialized(bh: Blackhole): Unit = {
    bloomFilter.add(42)
    bh.consume(bloomFilter.query(123))
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.MICROSECONDS)
  def specialized(bh: Blackhole): Unit = {
    specializedBloomFilter.add(42)
    bh.consume(specializedBloomFilter.query(123))
  }

}
