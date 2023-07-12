package cub.async.answers

import cub.async.tasks.bar
import cub.async.tasks.foo
import java.lang.Thread.sleep
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.*

class CThreadPool(val executor: ExecutorService) : CoroutineContext.Element, CoroutineContext.Key<CThreadPool> {
    companion object Key : CoroutineContext.Key<CThreadPool>

    private val smth = ConcurrentHashMap<CoroutineContext.Key<*>, CoroutineContext.Element>()

    override val key: CoroutineContext.Key<CThreadPool>
        get() = Key


    override fun <R> fold(initial: R, operation: (R, CoroutineContext.Element) -> R): R = operation(initial, this)

    override operator fun <E : CoroutineContext.Element> get(key: CoroutineContext.Key<E>): E? {
        return smth[key] as? E
    }

    override fun minusKey(key: CoroutineContext.Key<*>): CoroutineContext = this.apply { smth.remove(key) }

}

object CThreadPools {
    val DEFAULT = CThreadPool(Executors.newFixedThreadPool(10))
    val MAIN = CThreadPool(Executors.newSingleThreadExecutor())

    fun shutdown() {
        DEFAULT.executor.shutdown()
        MAIN.executor.shutdown()
        DEFAULT.executor.awaitTermination(1, TimeUnit.MINUTES)
        MAIN.executor.awaitTermination(1, TimeUnit.MINUTES)
    }
}

suspend fun <R> switchThreadPool(threadPool: CThreadPool, block: suspend () -> R): R {
    val originalThreadPool = coroutineContext[CThreadPool] ?: run {
        println("No executor?")
        CThreadPools.DEFAULT
    }
    return suspendCoroutine { cont ->
        threadPool.executor.submit {
            block.startCoroutine(Continuation(originalThreadPool) {
                originalThreadPool.executor.submit { cont.resumeWith(it) }
            })
        }
    }
}

suspend fun wow() {
    println("theWow#foo ${Thread.currentThread().id}")
    val f = foo()
    val b = switchThreadPool(CThreadPools.MAIN) {
        println("theWow#bar ${Thread.currentThread().id}")
        bar(f)
    }
    println("theWow#print ${Thread.currentThread().id}")
    println(b)
}

fun myLaunch(block: suspend () -> Unit) {
    CThreadPools.DEFAULT.executor.submit { block.startCoroutine(Continuation(CThreadPools.DEFAULT) {}) }
}

fun main() {
    myLaunch {
        myLaunch {
            wow()
        }
        wow()
    }
    sleep(3000)
    CThreadPools.shutdown()
}