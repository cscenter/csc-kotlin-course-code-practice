package cub.concurrent.answers

import java.util.concurrent.atomic.*
import java.util.concurrent.locks.*
import kotlin.concurrent.*
import kotlin.random.*

// Solution using lock
fun List<Int>.parallelSum(nThreads: Int): Int {
    require(nThreads > 0)
    if (isEmpty())
        return 0
    // It is better to handle this case separately to avoid creating new thread
    if (nThreads == 1)
        return sum()
    var sum = 0
    val lock = ReentrantLock()
    chunked(nThreads)
        .map {
            thread {
                val chunkSum = it.sum()
                lock.withLock { sum += chunkSum }
            }
        }
        .forEach {
            it.join()
        }
    return sum
}

// Alternative solution using `AtomicInteger` class.
fun List<Int>.parallelSumAtomic(nThreads: Int): Int {
    require(nThreads > 0)
    if (isEmpty())
        return 0
    if (nThreads == 1)
        return sum()
    val sum = AtomicInteger(0)
    chunked(nThreads)
        .map {
            thread {
                sum.addAndGet(it.sum())
            }
        }
        .forEach {
            it.join()
        }
    return sum.get()
}

fun main() {
    check(listOf<Int>().parallelSum(nThreads = 1) == 0)

    check(listOf(1, 2, 3).parallelSum(nThreads = 1) == 6)

    val list = (0 .. 4000).toList()
    check(list.parallelSum(nThreads = 4) == list.sum())

    val randomList = sequence {
        while (true) {
            yield(Random.nextInt() % 100)
        }
    }.take(16).toList()

    check(randomList.parallelSum(nThreads = 4) == randomList.sum())
}