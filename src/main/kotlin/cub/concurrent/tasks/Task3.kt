package cub.concurrent.tasks

import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.concurrent.thread

/*
 * Implement parallel version of forEach function, which applies given lambda to all elements of the collection.
 * Your implementation should distribute the work between threads dynamically.
 * That is you should not split the list into chunks of equal size and pass each chunk to a separate thread.
 * Instead, create a thread pool and in each thread handle elements one-by-one.
 *
 * Parameter `nThreads` specifies the number of threads that should be used to perform the job.
 *
 */
fun<T> Collection<T>.parallelForEach(nThreads: Int, block: (T) -> Unit) {
    require(nThreads > 0)
    TODO()
}

fun main() {
    (1 .. 10).toList().parallelForEach(4) { i ->
        println("${i * i}")
    }
}