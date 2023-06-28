package cub.concurrent.answers

import kotlin.concurrent.*
import kotlinx.atomicfu.*
import kotlin.random.Random

fun parallelSum(list: List<Int>, nThreads: Int): Int {
    require(nThreads > 0)
    if (list.isEmpty())
        return 0
    //
    if (nThreads == 1)
        return list.sum()
    val sum = atomic(0)
    list.chunked(nThreads)
        .map {
            thread {
                sum += it.sum()
            }
        }
        .forEach {
            it.join()
        }
    return sum.value
}

fun main() {
    check(cub.concurrent.tasks.parallelSum(listOf(), nThreads = 1) == 0)

    check(cub.concurrent.tasks.parallelSum(listOf(1, 2, 3), nThreads = 1) == 6)

    val list = (0 .. 4000).toList()
    check(cub.concurrent.tasks.parallelSum(list, nThreads = 4) == list.sum())

    val randomList = sequence { yield(Random.nextInt() % 100) }.take(4000).toList()
    check(cub.concurrent.tasks.parallelSum(randomList, nThreads = 4) == randomList.sum())
}