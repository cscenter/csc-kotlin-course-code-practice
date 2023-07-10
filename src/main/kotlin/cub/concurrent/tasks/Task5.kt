package cub.concurrent.tasks

import kotlin.concurrent.thread

/*
 * Implement TransactionalBankAccount class.
 *
 * It should implement BankAccount interface, and, in addition to that, also provide `transaction` function.
 * This function should allow to perform a series of actions on a bank account atomically in one transaction.
 *
 * Consider the following example:
 *
 *    val account = TransactionalBankAccount()
 *    val t1 = thread {
 *       account.transaction {
 *           withdraw(1_000)
 *           withdraw(1_000)
 *       }
 *    }
 *    val t2 = thread {
 *       account.deposit(1_500)
 *    }
 *    t1.join(); t2.join()
 *    println(account.balance)
 *
 * In this program, the only possible outcome should be 1_500,
 * because under any interleaving of threads it is not possible
 * to withdraw 1_000 + 1_000 = 2_000 in a single transaction.
 *
 * Compare with the following example:
 *
 *    val account = TransactionalBankAccount()
 *    val t1 = thread {
 *       account.withdraw(1_000)
 *       account.withdraw(1_000)
 *    }
 *    val t2 = thread {
 *       account.deposit(1_500)
 *    }
 *    t1.join(); t2.join()
 *    println(account.balance)
 *
 * Here the result can be either 1_500 or 500.
 */

class TransactionalBankAccount : BankAccount {

    override val balance: Int
        get() = TODO()

    fun transaction(block: BankAccount.() -> Boolean): Boolean {
        TODO()
    }

    override fun deposit(amount: Int) {
        transaction { deposit(amount); true }
    }

    override fun withdraw(amount: Int): Boolean {
        return transaction { withdraw(amount) }
    }

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

fun TransactionalBankAccount.buyWithCashback(price: Int, cashbackPercentage: Int): Boolean = this.transaction {
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
        account.buyWithCashback(20_000, 5)
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