package pl.szczygieldev.cart.infrastructure.adapter.out.messaging

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import pl.szczygieldev.cart.domain.CartEvent
import pl.szczygieldev.cart.infrastructure.adapter.out.messaging.mapper.CartEventMapper
import pl.szczygieldev.ecommercelibrary.messaging.StoreAndForwardEventPublisher
import pl.szczygieldev.ecommercelibrary.outbox.Outbox


@Component("cartModule.CartEventPublisher")
internal class CartEventPublisher(eventPublisher: ApplicationEventPublisher, outbox: Outbox) :
    StoreAndForwardEventPublisher<CartEvent>(eventPublisher, outbox, CartEventMapper())