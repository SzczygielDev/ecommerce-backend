package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.integration.payments

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.ProcessPaymentCommand
import pl.szczygieldev.ecommercebackend.domain.PaymentTransaction
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.command.MediatorFacade
import pl.szczygieldev.ecommercebackend.infrastructure.integration.payments.PaymentNotification
import java.time.Instant

@Component
class MockPaymentNotifierService(val mediatorFacade: MediatorFacade) {
    private val logger = KotlinLogging.logger {}

    @EventListener
    suspend fun handlePaymentNotification(paymentNotification: PaymentNotification) {
        logger.info { "Received payment notification='$paymentNotification'" }
        mediatorFacade.send(
            ProcessPaymentCommand(
                paymentNotification.paymentId,
                PaymentTransaction(
                    paymentNotification.id,
                    paymentNotification.amount,
                    Instant.now()
                )
            )
        )
    }
}