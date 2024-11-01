package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.messaging

import arrow.core.raise.either
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import pl.szczygieldev.ecommercebackend.application.port.`in`.OrderMailUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.SendOrderConfirmationMailCommand
import pl.szczygieldev.ecommercebackend.domain.error.AppError
import pl.szczygieldev.ecommercebackend.domain.event.*
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.command.MediatorFacade
import pl.szczygieldev.shared.ddd.core.DomainEventHandler
@Component
class OrderEventHandler(
    val mediator: MediatorFacade
) :
    DomainEventHandler<OrderEvent> {
    companion object {
        private val log = KotlinLogging.logger { }
    }

    @EventListener
    override suspend fun handleEvent(domainEvent: OrderEvent) = either<AppError, Unit> {
        when (domainEvent) {
            is OrderCreated -> {}
            is OrderAccepted -> {}
            is OrderCanceled -> {}
            is OrderPackaged -> {}
            is OrderPackagingStarted -> {}
            is OrderRejected -> {}
            is OrderPaymentReceived -> {}
            is OrderInvalidAmountPaid -> {}
            is OrderPaid -> {
                mediator.sendAsync(SendOrderConfirmationMailCommand(domainEvent.orderId))
            }
            is OrderDeliveryStatusChanged -> {}
        }
    }.fold({
        log.error { "Event handling failed=${domainEvent}" }
    }, {
        log.info { "Event handled=${domainEvent}" }
    })
}