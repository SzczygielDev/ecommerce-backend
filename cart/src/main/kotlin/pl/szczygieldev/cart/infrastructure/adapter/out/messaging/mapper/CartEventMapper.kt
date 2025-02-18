package pl.szczygieldev.cart.infrastructure.adapter.out.messaging.mapper

import pl.szczygieldev.cart.CartSubmittedEvent
import pl.szczygieldev.cart.domain.CartCreated
import pl.szczygieldev.cart.domain.CartEvent
import pl.szczygieldev.cart.domain.CartSubmitted
import pl.szczygieldev.cart.domain.ItemAddedToCart
import pl.szczygieldev.cart.domain.ItemRemovedFromCart
import pl.szczygieldev.ecommercelibrary.messaging.IntegrationEvent
import pl.szczygieldev.ecommercelibrary.messaging.IntegrationEventMapper
import java.util.*

internal class CartEventMapper : IntegrationEventMapper<CartEvent> {
    override fun toIntegrationEvent(event: CartEvent): IntegrationEvent? {
        return when (event) {
            is CartCreated -> null
            is CartSubmitted -> CartSubmittedEvent(
                UUID.fromString(event.id),
                event.occurredOn,
                event.cartId.id,
                event.paymentServiceProvider.toString(),
                event.deliveryProvider.toString()
            )

            is ItemAddedToCart -> null
            is ItemRemovedFromCart -> null
        }
    }
}