package pl.szczygieldev.ecommercebackend.domain.event
import pl.szczygieldev.ddd.core.DomainEvent
import pl.szczygieldev.ecommercebackend.domain.CartId
import pl.szczygieldev.ecommercebackend.domain.ProductId
import java.time.Instant

sealed class CartEvent(val cartId: CartId) : DomainEvent<CartEvent>

class CartCreated(cartId: CartId) : CartEvent(cartId){
    override val occurredOn: Instant = Instant.now()
}

class ItemAddedToCart (val productId: ProductId,val  quantity: Int, cartId: CartId): CartEvent(cartId){
    override val occurredOn: Instant = Instant.now()
}

class ItemRemovedFromCart(val productId: ProductId,cartId: CartId) : CartEvent(cartId){
    override val occurredOn: Instant = Instant.now()
}

class CartSubmitted(cartId: CartId) : CartEvent(cartId){
    override val occurredOn: Instant = Instant.now()
}