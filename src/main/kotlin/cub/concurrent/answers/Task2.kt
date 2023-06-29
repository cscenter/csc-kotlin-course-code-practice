package cub.concurrent.answers

import kotlin.concurrent.*
import java.util.concurrent.locks.*

/*
 * Implement simple thread-safe blocking stack.
 * Use usual mutable list as an underlying storage.
 *
 * Methods can block the calling thread if they are executed concurrently with other methods.
 * Additionally, pop operation can also block the thread if the stack is empty ---
 * in this case it should wait until some value will be pushed to the stack.
 */
class SynchronizedBlockingStack<T> {
    private val list = mutableListOf<T>()
    private val lock = ReentrantLock()
    private val condition = lock.newCondition()

    fun push(value: T) = lock.withLock {
        list.add(value)
        condition.signal()
    }

    fun pop(): T = lock.withLock {
        while (list.isEmpty()) {
            condition.await()
        }
        list.removeLast()
    }

    fun top(): T? = lock.withLock {
        list.lastOrNull()
    }

    fun size(): Int = lock.withLock {
        list.size
    }
}

fun test1() {
    val stack = SynchronizedBlockingStack<Int>()
    thread {
        stack.push(1)
    }
    thread {
        stack.push(2)
        stack.push(3)
    }
    thread {
        val a = stack.pop()
        val b = stack.pop()
        val c = stack.pop()
        println("a=$a, b=$b, c=$c")
        // can print either:
        // a=3, b=2, a=1
        // a=1, b=3, c=2
        // a=3, b=1, a=2
    }
}

fun test2() {
    val stack = SynchronizedBlockingStack<Int>()
    thread {
        println("push(1)")
        stack.push(1)
    }
    thread {
        val r = stack.pop()
        println("pop(): $r")
    }
    // can only print lines in the following sequence:
    // push(1), pop():1
}

fun main() {
    test1()
    test2()
}