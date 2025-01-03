package pl.szczygieldev.order.domain

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import java.math.BigDecimal
import java.net.URL
import java.time.Instant
import java.util.UUID

internal class PaymentTests : BehaviorSpec({
    isolationMode = IsolationMode.InstancePerLeaf
    given("a new payment") {
        val paymentId = PaymentId(UUID.randomUUID().toString())
        val amount = BigDecimal.TEN
        val url = URL("http://localhost:8080/")
        val psp = PaymentServiceProvider.MOCK_PSP
        val payment = Payment.create(paymentId, amount, url, psp)

        then("is unpaid") {
            payment.isPaid.shouldBe(false)
        }
        then("status is ${PaymentStatus.UNPAID}") {
            payment.status.shouldBe(PaymentStatus.UNPAID)
        }
        then("rest amount is equal to total amount") {
            payment.restAmount.compareTo(amount).shouldBe(0)
        }

        `when`("its paid with multiple transactions"){
            val firstPaymentTransactionAmount = BigDecimal.valueOf(2.0)
            val secondPaymentTransactionAmount = BigDecimal.valueOf(2.0)
            payment.registerTransaction(
                PaymentTransaction(
                    PaymentTransactionId(UUID.randomUUID().toString()), firstPaymentTransactionAmount,
                    Instant.now()
                )
            )
            payment.registerTransaction(
                PaymentTransaction(
                    PaymentTransactionId(UUID.randomUUID().toString()), secondPaymentTransactionAmount,
                    Instant.now()
                )
            )
            val paidAmount = firstPaymentTransactionAmount + secondPaymentTransactionAmount
            then("sumOfTransactions sums up all registered transactions"){
                payment.sumOfTransactions.compareTo(paidAmount).shouldBe(0)
            }
            then("rest amount is difference between total and paid amount") {
                val calculatedRestAmount = payment.amount.minus(paidAmount)
                payment.restAmount.compareTo(calculatedRestAmount).shouldBe(0)
            }
        }


        `when`("its paid with invalid amount") {
            val firstPaymentTransactionAmount = BigDecimal.valueOf(2.0)
            val secondPaymentTransactionAmount = BigDecimal.valueOf(2.0)
            payment.registerTransaction(
                PaymentTransaction(
                    PaymentTransactionId(UUID.randomUUID().toString()), firstPaymentTransactionAmount,
                    Instant.now()
                )
            )
            payment.registerTransaction(
                PaymentTransaction(
                    PaymentTransactionId(UUID.randomUUID().toString()), secondPaymentTransactionAmount,
                    Instant.now()
                )
            )

            then("status is ${PaymentStatus.INVALID_AMOUNT}") {
                payment.status.shouldBe(PaymentStatus.INVALID_AMOUNT)
            }
        }
        `when`("its paid with correct amount") {
            val amountPaid = BigDecimal.TEN
            payment.registerTransaction(
                PaymentTransaction(
                    PaymentTransactionId(UUID.randomUUID().toString()), amountPaid,
                    Instant.now()
                )
            )
            then("status is ${PaymentStatus.PAID}") {
                payment.status.shouldBe(PaymentStatus.PAID)
            }

            then("rest amount is equal to 0"){
                payment.restAmount.compareTo(BigDecimal.ZERO).shouldBe(0)
            }
            then("sumOfTransactions is equal to amount"){
                payment.sumOfTransactions.compareTo(amount).shouldBe(0)
            }
        }
    }
    given("a payment with zero amount"){
        val paymentId = PaymentId(UUID.randomUUID().toString())
        val amount = BigDecimal.ZERO
        val url = URL("http://localhost:8080/")
        val psp = PaymentServiceProvider.MOCK_PSP


        val exception = shouldThrow<IllegalArgumentException> {
             Payment.create(paymentId, amount, url, psp)
        }

        then("IllegalArgumentException is thrown"){
            exception.message.shouldStartWith("Payment amount must be positive value")
        }
    }
    given("a payment with negative amount"){
        val paymentId = PaymentId(UUID.randomUUID().toString())
        val amount = BigDecimal.valueOf(-10)
        val url = URL("http://localhost:8080/")
        val psp = PaymentServiceProvider.MOCK_PSP


        val exception = shouldThrow<IllegalArgumentException> {
            Payment.create(paymentId, amount, url, psp)
        }

        then("IllegalArgumentException is thrown"){
            exception.message.shouldStartWith("Payment amount must be positive value")
        }
    }

})