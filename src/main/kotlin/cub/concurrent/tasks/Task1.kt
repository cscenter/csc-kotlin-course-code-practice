package cub.concurrent.tasks

import kotlin.random.Random

/*
 * Implement parallel sum for the list.
 * The function should split the list into chunks of equal size,
 * calculate the sum of each chunk into separate thread,
 * and then merge the results to get the final sum.
 * Please, do not allocate additional list to store sums of chunks,
 * use single variable to store the total sum and use
 * the synchronization primitives we studied on the lecture to
 * safely update this variable from different threads.
 * You can use any of the following: locks, synchronized, atomics, etc.
 *
 * NOTE 1: we haven't covered atomics in the first lecture, but you can still try use them in this task
 *   (check the documentation of `java.util.concurrent.atomic` package).
 * NOTE 2: for this task it is ok to use `java.util.concurrent.atomic.AtomicInteger` class instead of AtomicFU library.
 *
 * Parameter `nThreads` specifies the number of threads that should be used to calculate the sum.
 *
 * You can assume that the total sum does not overflow the range of `Int` type.
 *
 */
fun List<Int>.parallelSum(nThreads: Int): Int {
    require(nThreads > 0)
    TODO("Implement your solution here")
}

fun main() {
    check(listOf<Int>().parallelSum(nThreads = 1) == 0)

    check(listOf(1, 2, 3).parallelSum(nThreads = 1) == 6)

    val list = (0 .. 4000).toList()
    check(list.parallelSum(nThreads = 4) == list.sum())

    val randomList = sequence { yield(Random.nextInt() % 100) }.take(4000).toList()
    check(randomList.parallelSum(nThreads = 4) == randomList.sum())
}