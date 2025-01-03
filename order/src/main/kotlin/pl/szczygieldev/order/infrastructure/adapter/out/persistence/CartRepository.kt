package pl.szczygieldev.order.infrastructure.adapter.out.persistence

import org.springframework.stereotype.Repository
import pl.szczygieldev.ecommercelibrary.eventstore.EventStore
import pl.szczygieldev.order.application.port.out.Carts
import pl.szczygieldev.order.domain.Cart
import pl.szczygieldev.order.domain.CartId
import pl.szczygieldev.order.domain.event.CartEvent
import java.util.UUID

@Repository
internal class CartRepository(val eventStore: EventStore) : Carts {

    override fun nextIdentity(): CartId = CartId(UUID.randomUUID().toString())
    override fun findById(id: CartId): Cart? {
        val eventsForCart = eventStore.getEvents<CartEvent>(id) ?: return null

        return Cart.fromEvents(id, eventsForCart)
    }

    override fun save(cart: Cart, version: Int) {
        val occurredEvents = cart.occurredEvents()
        eventStore.appendEvents(cart.cartId,occurredEvents,version)
        cart.clearOccurredEvents()
    }
}