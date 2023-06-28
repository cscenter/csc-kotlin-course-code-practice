package cub.concurrent.tasks

import kotlin.random.Random

/* Implement parallel sum for the list.
 * The function should split the list into chunks of equal size,
 * calculate the sum of each chunk into separate thread,
 * and then merge the results to get the final sum.
 * Please, do not allocate additional list to store sums of chunks,
 * use single variable to store the total sum and use
 * the synchronization primitives we studied on the lecture to
 * safely update this variable from different threads.
 *
 * The additional parameter `nThreads` specifies the number of threads
 * that should be used to calculate the sum.
 *
 * You can assume that the total sum does not overflow the range of `Int` type.
 *
 * The output format:
 * NN
 * (single number -- sum of the list)
 */
fun parallelSum(list: List<Int>, nThreads: Int): Int {
    require(nThreads > 0)
    TODO("Implement your solution here")
}

fun main() {
    check(parallelSum(listOf(), nThreads = 1) == 0)

    check(parallelSum(listOf(1, 2, 3), nThreads = 1) == 6)

    val list = (0 .. 4000).toList()
    check(parallelSum(list, nThreads = 4) == list.sum())

    val randomList = sequence { yield(Random.nextInt() % 100) }.take(4000).toList()
    check(parallelSum(randomList, nThreads = 4) == randomList.sum())
}