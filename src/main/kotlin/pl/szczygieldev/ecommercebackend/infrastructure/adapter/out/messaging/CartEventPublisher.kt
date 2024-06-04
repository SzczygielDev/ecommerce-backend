package pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.messaging

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import pl.szczygieldev.ecommercebackend.domain.event.CartEvent
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.messaging.publisher.StoreAndForwardEventPublisher
import pl.szczygieldev.shared.outbox.Outbox

@Component
class CartEventPublisher(eventPublisher: ApplicationEventPublisher, outbox: Outbox) :
    StoreAndForwardEventPublisher<CartEvent>(eventPublisher, outbox)