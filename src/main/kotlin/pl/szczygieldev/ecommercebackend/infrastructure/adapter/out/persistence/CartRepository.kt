package pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.persistence

import org.springframework.stereotype.Repository
import pl.szczygieldev.ecommercebackend.application.port.out.Carts
import pl.szczygieldev.ecommercebackend.domain.Cart
import pl.szczygieldev.ecommercebackend.domain.CartId
import pl.szczygieldev.ecommercebackend.domain.event.CartEvent
import pl.szczygieldev.shared.eventstore.EventStore
import java.util.UUID

@Repository
class CartRepository(val eventStore: EventStore) : Carts {

    override fun nextIdentity(): CartId = CartId(UUID.randomUUID().toString())
    override fun findById(id: CartId): Cart? {
        val eventsForCart = eventStore.getEvents<CartEvent>(id) ?: return null

        return Cart.fromEvents(id, eventsForCart)
    }

    override fun save(cart: Cart, version: Int) {
        val occuredEvents = cart.occurredEvents()
        eventStore.appendEvents(cart.cartId,occuredEvents,version)
        cart.clearOccurredEvents()
    }
}