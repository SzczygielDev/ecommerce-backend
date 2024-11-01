package pl.szczygieldev.ecommercebackend.application.port.`in`.query.model

import pl.szczygieldev.ecommercebackend.domain.*
import java.time.Instant

data class OrderProjection(
    val orderId: OrderId,
    val cartId: CartId,
    val status: OrderStatus,
    val paymentProjection: PaymentProjection,
    val delivery: Delivery,
    val createdAt: Instant,
    val items: List<OrderItemProjection>
) {
    /*
    *   We want to enrich the projection model of order item because product title and/or price can change in the future, which can lead to inconsistency in past orders.
    * */
    data class OrderItemProjection(
        val productId: ProductId,
        var title: ProductTitle,
        val price: ProductPrice,
        val quantity: Int,
        val imageId: ImageId
    )
}