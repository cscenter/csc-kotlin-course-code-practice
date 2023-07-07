package cub.concurrent.answers

import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread

private class BankAccountState : BankAccount {

    override var balance: Int = 0
        private set

    override fun deposit(amount: Int) {
        balance += amount
    }

    override fun withdraw(amount: Int): Boolean {
        return if (balance - amount >= 0) {
            balance -= amount
            true
        } else false
    }

    fun copy() = BankAccountState().also { it.balance = balance }

}

class TransactionalBankAccount : BankAccount {

    private val account = AtomicReference(BankAccountState())

    override val balance: Int
        get() = account.get().balance

    fun transaction(block: BankAccount.() -> Boolean): Boolean {
        do {
            val currentAccount = account.get()
            val newAccount = currentAccount.copy()
            if (!block(newAccount))
                return false
        } while (!account.compareAndSet(currentAccount, newAccount))
        return true
    }

    override fun deposit(amount: Int) {
        transaction { deposit(amount); true }
    }

    override fun withdraw(amount: Int): Boolean {
        return transaction { withdraw(amount) }
    }
}

fun TransactionalBankAccount.buyWithCashback(price: Int, cashbackPercentage: Int): Boolean = this.transaction {
    val cashback = (price * cashbackPercentage + 50) / 100
    if (withdraw(price)) {
        deposit(cashback)
        true
    } else false
}

fun transactionalBankAccountTest0() {
    val account = TransactionalBankAccount()
    account.deposit(1_500)
    account.transaction {
        withdraw(1_000)
        withdraw(1_000)
    }
    check(account.balance == 1_500)
}

fun transactionalBankAccountTest1() {
    val account = TransactionalBankAccount()
    val t1 = thread {
        account.transaction {
            withdraw(1_000)
            withdraw(1_000)
        }
    }
    val t2 = thread {
        account.deposit(1_500)
    }
    t1.join(); t2.join()
    check(account.balance == 1_500)
}

fun transactionalBankAccountTest2() {
    val account = TransactionalBankAccount()
    val t1 = thread {
        account.withdraw(1_000)
        account.withdraw(1_000)
    }
    val t2 = thread {
        account.deposit(1_500)
    }
    t1.join(); t2.join()
    check(account.balance == 1_500 || account.balance == 500)
}

fun cub.concurrent.tasks.TransactionalBankAccount.buyWithCashback(price: Int, cashbackPercentage: Int): Boolean = this.transaction {
    val cashback = (price * cashbackPercentage + 50) / 100
    if (withdraw(price)) {
        deposit(cashback)
        true
    } else false
}

fun transactionalBankAccountTest3() {
    val account = TransactionalBankAccount()
    account.deposit(10_000)
    val t1 = thread {
        account.buyWithCashback(1_000, 20)
    }
    val t2 = thread {
        account.buyWithCashback(5_000, 5)
    }
    val t3 = thread {
        account.buyWithCashback(10_000, 5)
    }
    t1.join(); t2.join(); t3.join()
    check(account.balance == 4_450)
}

fun main() {
    transactionalBankAccountTest0()
    transactionalBankAccountTest1()
    transactionalBankAccountTest2()
    transactionalBankAccountTest3()
}