package cub.concurrent.tasks

import kotlin.concurrent.thread


interface BankAccount {
    /**
     * Returns current balance.
     */
    val balance: Int

    /**
     * Adds the given amount to the balance.
     */
    fun deposit(amount: Int)

    /**
     * Tries to withdraw the given amount from the balance.
     *
     * @return true if withdraw is successful, false otherwise
     */
    fun withdraw(amount: Int): Boolean
}

// Please implement thread-safe DebitBankAccount class.
// This class should implement BankAccount interface.
// The balance of a debit account should always remain positive.
class DebitBankAccount : BankAccount {

    override val balance: Int
        get() = TODO()

    override fun deposit(amount: Int) {
        TODO()
    }

    override fun withdraw(amount: Int): Boolean {
        TODO()
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