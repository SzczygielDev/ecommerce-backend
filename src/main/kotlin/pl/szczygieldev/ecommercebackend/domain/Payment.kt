package pl.szczygieldev.ecommercebackend.domain

import java.math.BigDecimal
import java.net.URL

class Payment(
    val id: PaymentId, val amount: BigDecimal, val url: URL,
    val paymentServiceProvider: PaymentServiceProvider
) {
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

        _status = if (sumOfTransactions.compareTo(amount) == 0) {
            PaymentStatus.PAID
        } else if (sumOfTransactions.compareTo(BigDecimal.ZERO) == 0) {
            PaymentStatus.UNPAID
        } else {
            PaymentStatus.INVALID_AMOUNT
        }
    }

}