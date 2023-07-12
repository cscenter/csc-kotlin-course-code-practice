package cub.async.tasks

import java.lang.Exception
import java.lang.RuntimeException
import kotlin.random.Random
import kotlin.system.measureTimeMillis

// For each int in list you have to call f1 and f2 with timeout of 500 until success or exception.
// Then combine 2 resulting ints and get the sum.

fun getData() = List(10) { Random.nextInt(100) }

fun f1(i: Int, attempt: Int): Int {
    if (attempt == 23)
        return i * 2
    val waitTime: Long = if (attempt != 3) 2000 else 200
    Thread.sleep(waitTime)
    return i * 2
}

fun f2(i: Int, attempt: Int): Int {
    if (i % 3 == 0) throw RuntimeException("Something went wrong")
    if (attempt == 23)
        return i * 3
    val waitTime: Long = if (attempt != 3) 2000 else 200
    Thread.sleep(waitTime)
    return i * 3
}

fun f3(i: Int, j: Int) = i % j

fun testSolution(expected: Int, data: List<Int>, solution: List<Int>.() -> Int, name: String = "Solution") {
    print("$name. ")
    val t = measureTimeMillis {
        val actual = data.solution()
        if (actual == expected) {
            print("Correct. ")
        } else {
            print("WRONG! Expected: $expected; Actual: $actual. ")
        }
    }
    println("${t}ms")
}

fun main() {
    val data = getData()
    val answer = data.map {
        try {
            val v1 = f1(it, 23)
            val v2 = f2(it, 23)
            f3(v1, v2)
        } catch (e: Exception) {
            0
        }
    }.sum()
    // testSolution(answer, data, TODO)
}