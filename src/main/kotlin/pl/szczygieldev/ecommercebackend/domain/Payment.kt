package pl.szczygieldev.ecommercebackend.domain

import java.math.BigDecimal
import java.net.URL

class Payment private constructor(
    val id: PaymentId, val amount: BigDecimal, val url: URL,
    val paymentServiceProvider: PaymentServiceProvider
) {
    companion object {
        fun create(
            id: PaymentId, amount: BigDecimal, url: URL,
            paymentServiceProvider: PaymentServiceProvider
        ): Payment {
            require(amount > BigDecimal.ZERO) { "Payment amount must be positive value, provided=$amount" }
            return Payment(id, amount, url, paymentServiceProvider)
        }
    }

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

    fun copy(): Payment {
        val copy = create(id.copy(), amount, url, paymentServiceProvider)
        transactions.forEach { paymentTransaction ->
            copy.registerTransaction(paymentTransaction.copy())
        }
        return copy
    }
}