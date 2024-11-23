package pl.szczygieldev.order.infrastructure.adapter.out.messaging

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import pl.szczygieldev.order.domain.event.CartEvent
import pl.szczygieldev.shared.messaging.StoreAndForwardEventPublisher
import pl.szczygieldev.shared.outbox.Outbox

@Component
class CartEventPublisher(eventPublisher: ApplicationEventPublisher, outbox: Outbox) :
    StoreAndForwardEventPublisher<CartEvent>(eventPublisher, outbox)