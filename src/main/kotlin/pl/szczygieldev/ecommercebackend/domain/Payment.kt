package pl.szczygieldev.ecommercebackend.domain

import java.math.BigDecimal

class Payment(val amount: BigDecimal, val paymentServiceProvider: PaymentServiceProvider) {
    private var status: PaymentStatus = PaymentStatus.UNPAID
    private var transactions = mutableListOf<PaymentTransaction>()

    val isPaid: Boolean
        get() = status == PaymentStatus.PAID
    val restAmount: BigDecimal
        get() {
            val result = amount.minus(sumOfTransactions)
            if (result <= BigDecimal.ZERO) {
                return BigDecimal.ZERO
            } else {
                return result
            }
        }
    val sumOfTransactions: BigDecimal
        get() = transactions.sumOf { transaction -> transaction.amount }

    fun registerTransaction(paymentTransaction: PaymentTransaction) {
        transactions.add(paymentTransaction)

        if (sumOfTransactions == amount) {
            status = PaymentStatus.PAID
        } else if (sumOfTransactions == BigDecimal.ZERO) {
            status = PaymentStatus.UNPAID
        } else {
            status = PaymentStatus.INVALID_AMOUNT
        }
    }

}