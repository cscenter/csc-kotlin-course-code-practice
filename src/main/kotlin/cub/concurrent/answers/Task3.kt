package cub.concurrent.answers

import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.concurrent.thread

fun<T> Collection<T>.parallelForEach(nThreads: Int, block: (T) -> Unit) {
    require(nThreads > 0) { "The number of threads must be > 0" }
    val queue = ConcurrentLinkedQueue(this)
    val threadPool = (0 until nThreads).map { _ ->
        thread {
            while (true) {
                block(queue.poll() ?: return@thread)
            }
        }
    }
    threadPool.forEach { it.join() }
}

fun main() {
    (1 .. 10).toList().parallelForEach(4) { i ->
        println("${i * i}")
    }
}