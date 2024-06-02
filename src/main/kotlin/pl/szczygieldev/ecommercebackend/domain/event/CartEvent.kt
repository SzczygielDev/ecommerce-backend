package pl.szczygieldev.ecommercebackend.domain.event

import pl.szczygieldev.ddd.core.DomainEvent
import pl.szczygieldev.ecommercebackend.domain.CartId
import pl.szczygieldev.ecommercebackend.domain.ProductId
import java.time.Instant

sealed class CartEvent() : DomainEvent<CartEvent>()
class CartCreated(val cartId: CartId) : CartEvent(){
  override fun toString(): String {
    return "CartCreated(id=$id occuredOn=$occurredOn cartId=$cartId)"
  }
}
class ItemAddedToCart(val productId: ProductId, val quantity: Int, val cartId: CartId) : CartEvent(){
  override fun toString(): String {
    return "ItemAddedToCart(id=$id occuredOn=$occurredOn productId=$productId, quantity=$quantity, cartId=$cartId)"
  }
}
class ItemRemovedFromCart(val productId: ProductId, val cartId: CartId) : CartEvent(){
  override fun toString(): String {
    return "ItemRemovedFromCart(id=$id occuredOn=$occurredOn productId=$productId, cartId=$cartId)"
  }
}
class CartSubmitted(val cartId: CartId) : CartEvent(){
  override fun toString(): String {
    return "CartSubmitted(id=$id occuredOn=$occurredOn cartId=$cartId)"
  }
}
