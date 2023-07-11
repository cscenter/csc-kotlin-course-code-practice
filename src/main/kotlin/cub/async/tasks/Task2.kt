package cub.async.tasks

import kotlin.random.Random

/*
 * The task is to implement an Asynchronous framework that would allow to launch callbacks concurrently.
 */

interface AsyncFramework {
    fun<T, R> async(call1: () -> T, call2: (T) -> R)
}

fun doSomething(): Int {
    println("1st thing")
    return Random.nextInt(123)
}

fun doOtherThing(arg: Int): Double {
    println("2nd thing")
    return arg.toDouble() / 23
}

fun main() {
    val o: AsyncFramework = TODO() // out instance of the Async framework
    o.async(::doSomething) { res ->
        o.async({ doOtherThing(res) }) { nextRes ->
            println("3rd thing")
        }
    }
}
