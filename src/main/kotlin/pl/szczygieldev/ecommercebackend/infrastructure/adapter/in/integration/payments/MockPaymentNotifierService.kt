package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.integration.payments

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import pl.szczygieldev.ecommercebackend.application.port.`in`.OrderPaymentUseCase
import pl.szczygieldev.ecommercebackend.domain.PaymentTransaction
import pl.szczygieldev.ecommercebackend.infrastructure.integration.payments.PaymentNotification
import java.time.Instant

@Component
class MockPaymentNotifierService(val orderPaymentUseCase: OrderPaymentUseCase) {
    private val logger = KotlinLogging.logger {}

    @EventListener
    suspend fun handlePaymentNotification(paymentNotification: PaymentNotification) {
        logger.info { "Received payment notification='$paymentNotification'" }
        orderPaymentUseCase.pay(
            paymentNotification.paymentId,
            PaymentTransaction(
                paymentNotification.id,
                paymentNotification.amount,
                Instant.now()
            )
        )
    }
}