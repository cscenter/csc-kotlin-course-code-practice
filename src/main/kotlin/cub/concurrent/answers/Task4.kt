package cub.concurrent.answers

import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

interface BankAccount {
    val balance: Int
    fun deposit(amount: Int)
    fun withdraw(amount: Int): Boolean
}

class DebitBankAccount : BankAccount {
    private val value = AtomicInteger(0)

    override val balance: Int
        get() = value.get()

    override fun deposit(amount: Int) {
        value.addAndGet(amount)
    }

    override fun withdraw(amount: Int): Boolean {
        do {
            val currentBalance = value.get()
            val newBalance = currentBalance - amount
            if (newBalance < 0) {
                return false
            }
        } while (!value.compareAndSet(currentBalance, newBalance))
        return true
    }

}

fun main() {
    val account = DebitBankAccount()
    val threads = Array<Thread?>(5) { null }
    threads[0] = thread {
        account.deposit(10_000)
    }
    threads[1] = thread {
        for (i in 1 .. 10)
            account.deposit(1_000)
    }
    threads[2] = thread {
        account.withdraw(1_000)
    }
    threads[3] = thread {
        for (i in 1 .. 4)
            account.withdraw(500)
    }
    threads[4] = thread {
        account.withdraw(30_000)
    }
    threads.forEach { it?.join() }
    check(account.balance == 17_000)
}