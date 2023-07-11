package cub.async.answers

import cub.async.tasks.Server
import cub.async.tasks.posts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.system.measureTimeMillis

fun getWorkers(): ExecutorService {
    val cores = Runtime.getRuntime().availableProcessors()
    println("#cores: $cores")
    return Executors.newFixedThreadPool(cores)
}

fun solutionUsingAsync() {
    val myAsync = MyAsyncFramework()
    for (text in posts) {
        myAsync.async({ Server.getToken()}) { token ->
            Server.submitPost(text, token)
        }
    }
    Thread.sleep(500) // ;^)
    myAsync.close()
}

fun threadsSolution() {
    val threadPool = getWorkers()

    for (text in posts) {
        val runnable = Runnable {
            val token = Server.getToken()
            val meta = Server.submitPost(text, token)
        }
        threadPool.execute(runnable)
    }

    threadPool.shutdown()
    threadPool.awaitTermination(1, TimeUnit.HOURS)
    println("All posts processed")
}

fun coroutinesSolution() {
    val scope = CoroutineScope(Dispatchers.IO)
    val job = scope.launch {
        for (text in posts) {
            launch {
                val token = suspendCoroutine { it.resume(Server.getToken()) }
                val meta = suspendCoroutine { it.resume(Server.submitPost(text, token)) }
            }
        }
    }
     runBlocking {
         job.join()
         println("All posts processed")
     }
}

fun main() {
    val timeThreads = measureTimeMillis {
        threadsSolution()
    }
    println("It took ${timeThreads.toDouble() / posts.size} on average")
    val timeCoroutines = measureTimeMillis {
        coroutinesSolution()
    }
    println("It took ${timeCoroutines.toDouble() / posts.size} on average")
    val timeAsync = measureTimeMillis {
        solutionUsingAsync()
    }
    println("It took ${timeAsync.toDouble() / posts.size} on average")
}