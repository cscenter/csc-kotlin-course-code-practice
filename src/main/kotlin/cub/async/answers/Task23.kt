package cub.async.answers

import cub.async.tasks.bar
import cub.async.tasks.foo
import java.lang.Thread.sleep
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.*

val theDEFAULT = Executors.newFixedThreadPool(2)
val theMAIN = Executors.newFixedThreadPool(1)

suspend fun <R> theSwitch(executorService: ExecutorService, block: suspend () -> R): R = suspendCoroutine { cont ->
    executorService.submit { block.startCoroutine(cont) }
}

fun theLaunch(block: suspend () -> Unit) {
    theDEFAULT.submit { block.startCoroutine(Continuation(EmptyCoroutineContext) {}) }
}

suspend fun theWow() {
    println("theWow#foo ${Thread.currentThread().id}")
    val f = foo()
    val b = theSwitch(theMAIN) {
        println("theWow#bar ${Thread.currentThread().id}")
        bar(f)
    }
    println("theWow#print ${Thread.currentThread().id}")
    println(b)
}

fun main() {
    theLaunch {
        theLaunch {
            theWow()
        }
        theWow()
    }
    sleep(1000)
    theMAIN.shutdown()
    theDEFAULT.shutdown()
    theMAIN.awaitTermination(1, TimeUnit.MINUTES)
    theDEFAULT.awaitTermination(1, TimeUnit.MINUTES)
}