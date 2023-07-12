package cub.async.answers

import cub.async.tasks.Task1
import cub.async.tasks.testSolution
import kotlinx.coroutines.*
import kotlin.concurrent.thread

open class Solution1<T : Comparable<T>> : Task1<T> {

    private fun partition(arr: MutableList<T>, l: Int, r: Int): Pair<Int, Int> {
        val pivot = arr[l]
        var ml = l
        var mr = l + 1
        for (i in (l + 1) until r) {
            // < | == | >
            if (arr[i] < pivot) {
                arr.swap(i, mr)
                arr.swap(mr, ml)
                ml++
                mr++
            } else if (arr[i] == pivot) {
                arr.swap(i, mr)
                mr++
            }
        }
        return ml to mr
    }

    override fun quickSort(arr: MutableList<T>) {
        quickSort(arr, 0, arr.size)
    }

    private fun quickSort(arr: MutableList<T>, l: Int, r: Int) {
        if (l >= r) return
        val (ml, mr) = partition(arr, l, r)
        quickSort(arr, l, ml)
        quickSort(arr, mr, r)
    }

    private fun MutableList<T>.swap(i: Int, j: Int) {
        val tmp = this[i]
        this[i] = this[j]
        this[j] = tmp
    }

    override fun quickSortParallel(arr: MutableList<T>) {
        quickSortParallel(arr, 0, arr.size)
    }

    private fun quickSortParallel(arr: MutableList<T>, l: Int, r: Int) {
        if (l >= r) return
        val (ml, mr) = partition(arr, l, r)
        val t = thread { quickSortParallel(arr, l, ml) }
        quickSortParallel(arr, mr, r)
        t.join()
    }

    override fun quickSortAsync(arr: MutableList<T>) {
        val scope = CoroutineScope(Dispatchers.Default)
        val job = scope.launch { quickSortAsync(arr, 0, arr.size) }
        runBlocking {
            job.join()
        }
    }

    fun CoroutineScope.quickSortAsync(arr: MutableList<T>, l: Int, r: Int) {
        if (l >= r) return
        val (ml, mr) = partition(arr, l, r)
        launch { quickSortAsync(arr, l, ml) }
        quickSortAsync(arr, mr, r)
    }
}

class AlternativeSolution1<T : Comparable<T>> : Solution1<T>() {
    override fun quickSortAsync(arr: MutableList<T>) {
        runBlocking {
            coroutineScope {
                quickSortAsync(arr, 0, arr.size)
            }
        }
    }
}

fun main() {
    repeat(10) {
        val solution1: Task1<Int> = Solution1()
        testSolution(solution1)
        val solution2: Task1<Int> = AlternativeSolution1()
        testSolution(solution2)
    }
}