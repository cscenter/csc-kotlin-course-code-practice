package cub.async.answers

import cub.async.tasks.Server
import cub.async.tasks.UI
import cub.async.tasks.posts
import kotlinx.coroutines.*
import java.lang.Runnable
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread
import kotlin.concurrent.withLock
import kotlin.system.measureTimeMillis

class ThreadsUI : UI(), Runnable {
    val lock = ReentrantLock()
    override fun run() {
        println("UI launched")
        while (true) {
            try {
                if (buffer.isNotEmpty()) {
                    lock.withLock {
                        println(buffer.toString())
                        buffer.delete(0, buffer.length)
                    }
                } else {
                    Thread.sleep(500)
                }
            } catch (ex: InterruptedException) {
                println("UI shutting down")
                break
            }
        }
    }

    fun update(meta: Server.Meta) {
        lock.withLock {
            buffer.appendLine("Post: ${meta.content}")
        }
    }
}

fun threadsSolutionWithUI() {
    val ui = ThreadsUI()
    val uiThread = thread { ui.run() }
    val threadPool = getWorkers()

    for (text in posts) {
        val runnable = Runnable {
            val token = Server.getToken()
            val meta = Server.submitPost(text, token)
            ui.update(meta)
        }
        threadPool.execute(runnable)
    }

    threadPool.shutdown()
    threadPool.awaitTermination(1, TimeUnit.HOURS)
    uiThread.interrupt()
}

class SimpleUI : UI() {
    fun draw() {
        if (buffer.isNotEmpty()) {
            println(buffer.toString())
            buffer.delete(0, buffer.length)
        }
    }

    fun update(meta: Server.Meta) {
        buffer.appendLine("Post: ${meta.content}")
    }
}

@OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
fun coroutineSolutionWithUI() {
    val ui = SimpleUI()

    val uiContext = newSingleThreadContext("UI")
    val scope = CoroutineScope(Dispatchers.Default)
    scope.launch(uiContext) {
        while (true) {
            ui.draw()
            delay(100)
        }
    }
    val job = scope.launch {
        for (text in posts) {
            launch {
                val token = withContext(Dispatchers.IO) {
                    Server.getToken()
                }
                val meta = withContext(Dispatchers.IO) {
                    Server.submitPost(text, token)
                }
                withContext(uiContext) {
                    ui.update(meta)
                }
            }
        }
    }
    runBlocking {
        job.join()
        println("All posts processed")
    }
    scope.cancel()
}

fun main() {
    val timeThreads = measureTimeMillis {
        threadsSolutionWithUI()
    }
    println("It took ${timeThreads.toDouble() / posts.size} on average")
    val timeCoroutines = measureTimeMillis {
        coroutineSolutionWithUI()
    }
    println("It took ${timeCoroutines.toDouble() / posts.size} on average")
}