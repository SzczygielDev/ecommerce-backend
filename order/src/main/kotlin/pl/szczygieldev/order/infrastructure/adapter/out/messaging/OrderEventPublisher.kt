package pl.szczygieldev.order.infrastructure.adapter.out.messaging

import org.springframework.stereotype.Component
import pl.szczygieldev.ecommercelibrary.command.Mediator
import pl.szczygieldev.ecommercelibrary.outbox.Outbox
import pl.szczygieldev.ecommercelibrary.outbox.StoreAndForwardEventPublisher
import pl.szczygieldev.order.domain.event.OrderEvent
import pl.szczygieldev.order.infrastructure.adapter.out.messaging.mapper.OrderEventMapper

@Component
internal class OrderEventPublisher(mediator: Mediator, outbox: Outbox) :
    StoreAndForwardEventPublisher<OrderEvent>(mediator, outbox, OrderEventMapper())