package pl.szczygieldev.order.domain.event

import pl.szczygieldev.ecommercelibrary.ddd.core.DomainEvent
import pl.szczygieldev.order.domain.CartId
import pl.szczygieldev.order.domain.DeliveryProvider
import pl.szczygieldev.order.domain.PaymentServiceProvider
import pl.szczygieldev.order.domain.ProductId

internal sealed class CartEvent() : DomainEvent<CartEvent>()
internal class CartCreated(val cartId: CartId) : CartEvent(){
  override fun toString(): String {
    return "CartCreated(id=$id occuredOn=$occurredOn cartId=$cartId)"
  }
}
internal class ItemAddedToCart(val productId: ProductId, val quantity: Int, val cartId: CartId) : CartEvent(){
  override fun toString(): String {
    return "ItemAddedToCart(id=$id occuredOn=$occurredOn productId=$productId, quantity=$quantity, cartId=$cartId)"
  }
}
internal class ItemRemovedFromCart(val productId: ProductId, val cartId: CartId) : CartEvent(){
  override fun toString(): String {
    return "ItemRemovedFromCart(id=$id occuredOn=$occurredOn productId=$productId, cartId=$cartId)"
  }
}
internal class CartSubmitted(val cartId: CartId, val paymentServiceProvider: PaymentServiceProvider, val deliveryProvider: DeliveryProvider) : CartEvent(){
  override fun toString(): String {
    return "CartSubmitted(id=$id occuredOn=$occurredOn cartId=$cartId paymentServiceProvider=$paymentServiceProvider deliveryProvider=$deliveryProvider)"
  }
}
