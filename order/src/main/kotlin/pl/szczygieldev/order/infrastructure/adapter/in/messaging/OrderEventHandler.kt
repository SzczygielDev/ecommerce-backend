package pl.szczygieldev.order.infrastructure.adapter.`in`.messaging

import arrow.core.raise.either
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import pl.szczygieldev.ecommercelibrary.command.CommandQueue
import pl.szczygieldev.ecommercelibrary.ddd.core.DomainEventHandler
import pl.szczygieldev.order.application.port.`in`.command.SendOrderConfirmationMailCommand
import pl.szczygieldev.order.domain.error.AppError
import pl.szczygieldev.order.domain.event.*
@Component
internal class OrderEventHandler(
    val queue: CommandQueue
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
                queue.push(SendOrderConfirmationMailCommand(domainEvent.orderId))
            }
            is OrderDeliveryStatusChanged -> {}
        }
    }.fold({
        log.error { "Event handling failed=${domainEvent}" }
    }, {
        log.info { "Event handled=${domainEvent}" }
    })
}