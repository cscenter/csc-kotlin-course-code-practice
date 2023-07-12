package cub.async.answers

import cub.async.tasks.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.lang.Exception

private suspend fun <T> theRetry(arg: Int, block: suspend (Int, Int) -> T): T {
    repeat(4) { // try 4 times
        try {
            return withTimeout(500) { // with timeout
                block(arg, it)
            }
        } catch (e: TimeoutCancellationException) { /* retry */ }
    }
    return block(arg, 5) // last time just invoke without timeout
}

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
fun List<Int>.coroutineSumThread(): Int {
    val sumContext = newSingleThreadContext("sum")
    var sum = 0
    runBlocking {
        supervisorScope {
            forEach { i ->
                launch(Dispatchers.Default + CoroutineExceptionHandler { _, e ->  }) {
                    val res1 = async { theRetry(i, ::f1) }
                    val res2 = async { theRetry(i, ::f2) }
                    val res = f3(res1.await(), res2.await())
                    withContext(sumContext) {
                        sum += res
                    }
                }
            }
        }
    }
    return sum
}

fun List<Int>.coroutineSumMutex(): Int {
    val mutex = Mutex()
    var sum = 0
    runBlocking {
        supervisorScope {
            forEach { i ->
                launch(Dispatchers.Default + CoroutineExceptionHandler { _, e ->  }) {
                    val res1 = async { theRetry(i, ::f1) }
                    val res2 = async { theRetry(i, ::f2) }
                    val res = f3(res1.await(), res2.await())
                    mutex.withLock {
                        sum += res
                    }
                }
            }
        }
    }
    return sum
}

fun List<Int>.coroutineSumChannel(): Int {
    val channel = Channel<Int>(capacity = 10, onBufferOverflow = BufferOverflow.SUSPEND)
    var sum = 0
    runBlocking {
        coroutineScope {
            launch {
                for (msg in channel) {
                    sum += msg
                }
            }
            supervisorScope {
                forEach { i ->
                    launch(Dispatchers.Default + CoroutineExceptionHandler { _, e -> }) {
                        val res1 = async { theRetry(i, ::f1) }
                        val res2 = async { theRetry(i, ::f2) }
                        val res = f3(res1.await(), res2.await())
                        channel.send(res)
                    }
                }
            }
            // will be called when supervisorScope is done
            channel.close()
        }
    }
    return sum
}

fun List<Int>.coroutineSumSharedFlow(): Int {
    val flow = MutableSharedFlow<Int>(onBufferOverflow = BufferOverflow.SUSPEND)
    var sum = 0
    runBlocking {
        coroutineScope {
            val collectorJob = launch {
                flow.collect {
                    sum += it
                }
            }
            supervisorScope {
                forEach { i ->
                    launch(Dispatchers.Default + CoroutineExceptionHandler { _, e -> }) {
                        val res1 = async { theRetry(i, ::f1) }
                        val res2 = async { theRetry(i, ::f2) }
                        val res = f3(res1.await(), res2.await())
                        flow.emit(res)
                    }
                }
            }
            collectorJob.cancel()
        }
    }
    return sum
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
    testSolution(answer, data, List<Int>::coroutineSumThread, "Special thread")
    testSolution(answer, data, List<Int>::coroutineSumMutex, "Mutex")
    testSolution(answer, data, List<Int>::coroutineSumChannel, "Channel")
    testSolution(answer, data, List<Int>::coroutineSumSharedFlow, "Flow")
}
