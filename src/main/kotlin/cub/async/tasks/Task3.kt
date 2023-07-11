package cub.async.tasks

import java.time.LocalDate
import kotlin.random.Random

/*
 * A list of posts is given. The task is to submit all of them to server as fast as possible.
 * Try to write three solutions: using threads, using your callback framework and using coroutines.
 * Server is thread-safe.
 */

const val postsCount = 100

val posts = List(postsCount) { i -> "Bot post #$i"}

internal val ALPHABET = ('a'..'z').joinToString()  + ('A'..'Z').joinToString()

object Server {
    fun getToken(): Token {
        Thread.sleep(300)
        val token = List(10) { ALPHABET.random() }.joinToString()
        return Token(token)
    }

    fun submitPost(text: String, token: Token): Meta {
        Thread.sleep(500)
        val id = Random.nextLong(1000)
        print(".")
        return Meta(id, text, LocalDate.now())
    }

    class Token(val token: String)

    class Meta(val id: Long, val content: String, val date: LocalDate)
}
