package pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.integration.payments

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import pl.szczygieldev.ecommercebackend.application.port.out.PaymentService
import pl.szczygieldev.ecommercebackend.domain.PaymentId
import pl.szczygieldev.ecommercebackend.domain.PaymentRegistration
import pl.szczygieldev.ecommercebackend.domain.PaymentServiceProvider
import pl.szczygieldev.ecommercebackend.domain.PaymentTransactionId
import pl.szczygieldev.ecommercebackend.infrastructure.integration.payments.Payment
import pl.szczygieldev.ecommercebackend.infrastructure.integration.payments.PaymentStatus
import pl.szczygieldev.ecommercebackend.infrastructure.integration.payments.PaymentNotification
import java.math.BigDecimal
import java.net.URL
import java.util.UUID

@Component
class MockPaymentService(val eventPublisher: ApplicationEventPublisher) : PaymentService {
    private val db = mutableMapOf<PaymentId, Payment>()
    private val paymentUrlBase = "http://localhost:64427/mockPayment/"
    
    companion object {
        private val log = KotlinLogging.logger { }
    }

    override fun registerPayment(
        amount: BigDecimal,
        paymentServiceProvider: PaymentServiceProvider,
        returnURL: URL
    ): PaymentRegistration {
        val result = when (paymentServiceProvider) {
            PaymentServiceProvider.MOCK_PSP -> {
                val paymentId =  PaymentId(UUID.randomUUID().toString())
                PaymentRegistration(
                    paymentId ,
                    URL("$paymentUrlBase${paymentId.id()}"),
                )
            }
        }
        db.put(result.id, Payment(result.id.id(), amount, BigDecimal.ZERO, result.url, PaymentStatus.NOT_PAID,returnURL))
        return result
    }

    override fun verifyPayment(paymentId: PaymentId) {
        log.info { "Payment verified " }
    }


    fun mockPayment(paymentId: PaymentId, amount: BigDecimal) {
        val foundPayment = db[paymentId] ?: throw RuntimeException("Payment for id='${paymentId.id}' not found!")
        val paymentTransactionId =PaymentTransactionId(UUID.randomUUID().toString())
        foundPayment.pay(amount)

       eventPublisher.publishEvent(PaymentNotification(paymentTransactionId,paymentId, amount))
    }

    fun getMockPayment(paymentId: PaymentId) : Payment? = db[paymentId]
}