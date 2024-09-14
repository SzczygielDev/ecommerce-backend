package pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.messaging

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import pl.szczygieldev.ecommercebackend.domain.event.OrderEvent
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.messaging.publisher.StoreAndForwardEventPublisher
import pl.szczygieldev.shared.outbox.Outbox

@Component
class OrderEventPublisher(eventPublisher: ApplicationEventPublisher, outbox: Outbox) :
    StoreAndForwardEventPublisher<OrderEvent>(eventPublisher, outbox)