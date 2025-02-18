package pl.szczygieldev.cart.infrastructure.adapter.out.persistence

import org.springframework.stereotype.Repository
import pl.szczygieldev.cart.application.port.out.Carts
import pl.szczygieldev.cart.domain.Cart
import pl.szczygieldev.cart.domain.CartEvent
import pl.szczygieldev.cart.domain.CartId
import pl.szczygieldev.ecommercelibrary.eventstore.EventStore
import java.util.UUID

@Repository("cartModule.CartRepository")
internal class CartRepository(val eventStore: EventStore) : Carts {

    override fun nextIdentity(): CartId = CartId(UUID.randomUUID())
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