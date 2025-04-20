package pl.szczygieldev.order.infrastructure.adapter.`in`.messaging

import arrow.core.raise.either
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import pl.szczygieldev.ecommercelibrary.command.CommandQueue
import pl.szczygieldev.ecommercelibrary.event.ReactiveAsyncEventHandler
import pl.szczygieldev.ecommercelibrary.eventstore.EventStore
import pl.szczygieldev.ecommercelibrary.messaging.InMemoryMessageQueue
import pl.szczygieldev.ecommercelibrary.messaging.config.MessageQueueConfig
import pl.szczygieldev.order.application.port.`in`.command.SendOrderConfirmationMailCommand
import pl.szczygieldev.order.domain.event.*

@Component
internal class OrderEventHandler(
    val queue: CommandQueue,
    objectMapper: ObjectMapper,
    eventStore: EventStore,
) :
    ReactiveAsyncEventHandler<OrderEvent>(
        OrderEvent::class, objectMapper, eventStore, InMemoryMessageQueue(
            MessageQueueConfig()
        )
    ) {
    companion object {
        private val log = KotlinLogging.logger { }
    }

    override suspend fun handle(notification: OrderEvent) {
        val domainEvent = notification

        try {
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
            log.info { "Event handled=${domainEvent}" }
        } catch (e: Exception) {
            log.error { "Event handling failed=${domainEvent}" }
        }
    }
}