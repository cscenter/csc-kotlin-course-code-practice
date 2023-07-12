package cub.async.answers

import java.lang.Exception
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class AsyncFrameworkResultClass {
    private val threadPool = Executors.newCachedThreadPool()

    fun <T> async(call1: () -> T, call2: (Result<T>) -> Unit) {
        val runnable = Runnable {
            val res = try {
                val t = call1()
                Success(t)
            } catch (e: Exception) {
                Error(e)
            }
            threadPool.submit({ call2(res) })
        }
        threadPool.submit(runnable)
    }

    fun close() {
        threadPool.shutdown()
        threadPool.awaitTermination(1, TimeUnit.HOURS)
    }
}

sealed class Result<R>

class Success<R>(val r: R) : Result<R>()

class Error<R>(val e: Exception) : Result<R>()

class AsyncFrameworkCallbackClass {
    private val threadPool = Executors.newCachedThreadPool()

    fun <T> async(call1: () -> T, call2: AsyncCallback<T>.() -> Unit) {
        val runnable = Runnable {
            val nextCallback = AsyncCallback<T>()
            nextCallback.call2()
            val trueCall2 = try {
                val t = call1()
                Runnable { nextCallback._onSuccess(t) }
            } catch (e: Exception) {
                Runnable { nextCallback._onError(e) }
            }
            threadPool.submit(trueCall2)
        }
        threadPool.submit(runnable)
    }

    fun close() {
        threadPool.shutdown()
        threadPool.awaitTermination(1, TimeUnit.HOURS)
    }

    inner class AsyncCallback<R> {
        internal var _onSuccess: (R) -> Unit = { }
        internal var _onError: (Exception) -> Unit = { throw it }

        fun onSuccess(callback: (R) -> Unit) {
            _onSuccess = callback
        }

        fun onError(callback: (Exception) -> Unit) {
            _onError = callback
        }
    }
}

fun callback() {
    val a = AsyncFrameworkCallbackClass()
    a.async({ 123 }) {
        onSuccess { res ->
            a.async({ res.toDouble() }) {
                onSuccess {
                    println(it)
                    thread { a.close() }
                }
            }
        }
        onError {
            println("ERROR!")
        }
    }
}

fun result() {
    val a = AsyncFrameworkResultClass()
    a.async({ 123 }) { res ->
        when (res) {
            is Success -> {
                a.async({ res.r.toDouble() }) { res2 ->
                    when (res2) {
                        is Success -> {
                            println(res2.r)
                            thread { a.close() }
                        }
                        else -> { }
                    }
                }
            }
            is Error -> {
                println("ERROR!")
            }
        }
    }
}

fun main() {
    callback()
    result()
}