package cub.async.tasks

import kotlin.random.Random
import kotlin.system.measureTimeMillis

/*
 * The task is to implement QuickSort algorithm in 3 ways:
 * - Classic
 * - Parallel
 * - Asynchronous (using coroutines)
 */

interface Task1<T : Comparable<T>> {
    fun quickSort(arr: MutableList<T>)

    fun quickSortParallel(arr: MutableList<T>)

    fun quickSortAsync(arr: MutableList<T>)
}

fun testSolution(solution: Task1<Int>) {
    print("Classic: ")
    testQuickSort(solution::quickSort)
    print("Parallel: ")
    testQuickSort(solution::quickSortParallel)
    print("Async: ")
    testQuickSort(solution::quickSortAsync)
}

fun testQuickSort(sorter: (MutableList<Int>) -> Unit, iterations: Int = 300, listSize: Int = 100) {
    var correct = true
    var lastList: MutableList<*>? = null
    var i = 0
    val times = mutableListOf<Long>()
    while (correct && i < iterations) {
        val list = MutableList(listSize) {
            Random.nextInt(0, listSize / 3)
        }
        val sortedList = list.sorted()
        val time = measureTimeMillis { sorter(list) }
        times.add(time)
        i++
        correct = list == sortedList
        lastList = list
    }
    if (!correct) {
        println("WRONG!")
        println(lastList)
    } else {
        println("correct. Avg time: ${times.average()}")
    }
}

fun main() {
    val solution: Task1<Int> = TODO()
    testSolution(solution)
}
