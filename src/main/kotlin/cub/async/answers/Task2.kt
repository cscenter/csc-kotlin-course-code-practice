package cub.async.answers

import cub.async.tasks.AsyncFramework
import cub.async.tasks.doOtherThing
import cub.async.tasks.doSomething
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class MyAsyncFramework : AsyncFramework {
    private val threadPool = Executors.newCachedThreadPool()

    override fun <T, R> async(call1: () -> T, call2: (T) -> R) {
        val runnable = Runnable {
            val t = call1()
            val runnable2 = Runnable {
                call2(t)
            }
            threadPool.submit(runnable2)
        }
        threadPool.submit(runnable)
    }

    fun close() {
        threadPool.shutdown()
        threadPool.awaitTermination(1, TimeUnit.HOURS)
    }
}

private val myAsync = MyAsyncFramework()

private fun <T, R> async(call1: () -> T, call2: (T) -> R) = myAsync.async(call1, call2)

fun main() {
    repeat(10) {
        async(::doSomething) { res ->
            async({ doOtherThing(res) }) { nextRes ->
                println("3rd thing")
            }
        }
    }
    myAsync.close()
}