package pl.szczygieldev.cart.infrastructure.adapter.out.messaging

import org.springframework.stereotype.Component
import pl.szczygieldev.cart.domain.CartEvent
import pl.szczygieldev.cart.infrastructure.adapter.out.messaging.mapper.CartEventMapper
import pl.szczygieldev.ecommercelibrary.command.Mediator
import pl.szczygieldev.ecommercelibrary.messaging.StoreAndForwardEventPublisher
import pl.szczygieldev.ecommercelibrary.outbox.Outbox


@Component("cartModule.CartEventPublisher")
internal class CartEventPublisher(mediator: Mediator, outbox: Outbox) :
    StoreAndForwardEventPublisher<CartEvent>(mediator, outbox, CartEventMapper())