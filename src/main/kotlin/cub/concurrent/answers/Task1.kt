package cub.concurrent.answers

import kotlin.concurrent.*
import kotlinx.atomicfu.*
import kotlin.random.Random

class Task1 {
    private val sum = atomic(0)

    fun parallelSum(list: List<Int>, nThreads: Int): Int {
        require(nThreads > 0)
        if (list.isEmpty())
            return 0
        // It is better to handle this case separately to avoid creating new threads, etc
        if (nThreads == 1)
            return list.sum()
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
}

fun main() {
    val task1 = Task1()
    check(task1.parallelSum(listOf(), nThreads = 1) == 0)

    check(task1.parallelSum(listOf(1, 2, 3), nThreads = 1) == 6)

    val list = (0 .. 4000).toList()
    check(task1.parallelSum(list, nThreads = 4) == list.sum())

    val randomList = sequence { yield(Random.nextInt() % 100) }.take(4000).toList()
    check(task1.parallelSum(randomList, nThreads = 4) == randomList.sum())
}