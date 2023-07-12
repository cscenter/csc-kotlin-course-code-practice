package cub.async.tasks

import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun foo() =
    suspendCoroutine {
        println("foo")
        it.resume(23)
    }

suspend fun bar(arg: Int) =
    suspendCoroutine {
        println("bar")
        it.resume(arg.toDouble())
    }

//fun wow() {
//    val f = foo()
//    val b = switchContext(anotherContext) { bar() }
//    println(b)
//}

fun main() {
//    myLaunch {
//        myLaunch {
//            wow()
//        }
//        wow()
//    }
}