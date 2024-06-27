package pl.szczygieldev.ecommercebackend.domain

import java.math.BigDecimal

class Payment(val amount: BigDecimal, val paymentServiceProvider: PaymentServiceProvider) {
    private var _status: PaymentStatus = PaymentStatus.UNPAID
    val status: PaymentStatus
        get() = _status
    private var transactions = mutableListOf<PaymentTransaction>()

    val isPaid: Boolean
        get() = status == PaymentStatus.PAID
    val restAmount: BigDecimal
        get() {
            val result = amount.minus(sumOfTransactions)
            return if (result <= BigDecimal.ZERO) {
                BigDecimal.ZERO
            } else {
                result
            }
        }
    val sumOfTransactions: BigDecimal
        get() = transactions.sumOf { transaction -> transaction.amount }

    fun registerTransaction(paymentTransaction: PaymentTransaction) {
        transactions.add(paymentTransaction)

        _status = if (sumOfTransactions == amount) {
            PaymentStatus.PAID
        } else if (sumOfTransactions == BigDecimal.ZERO) {
            PaymentStatus.UNPAID
        } else {
            PaymentStatus.INVALID_AMOUNT
        }
    }

}