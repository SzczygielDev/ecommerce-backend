package pl.szczygieldev.order.infrastructure.adapter.out.messaging

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import pl.szczygieldev.ecommercelibrary.messaging.StoreAndForwardEventPublisher
import pl.szczygieldev.ecommercelibrary.outbox.Outbox
import pl.szczygieldev.order.domain.event.OrderEvent
import pl.szczygieldev.order.infrastructure.adapter.out.messaging.mapper.OrderEventMapper

@Component
internal class OrderEventPublisher(eventPublisher: ApplicationEventPublisher, outbox: Outbox) :
    StoreAndForwardEventPublisher<OrderEvent>(eventPublisher, outbox, OrderEventMapper())