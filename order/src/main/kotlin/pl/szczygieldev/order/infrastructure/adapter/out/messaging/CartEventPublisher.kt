package pl.szczygieldev.order.infrastructure.adapter.out.messaging

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import pl.szczygieldev.ecommercelibrary.messaging.StoreAndForwardEventPublisher
import pl.szczygieldev.ecommercelibrary.outbox.Outbox
import pl.szczygieldev.order.domain.event.CartEvent

@Component
internal class CartEventPublisher(eventPublisher: ApplicationEventPublisher, outbox: Outbox) :
    StoreAndForwardEventPublisher<CartEvent>(eventPublisher, outbox)