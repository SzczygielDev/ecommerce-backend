package pl.szczygieldev.ecommercebackend.application.model

import pl.szczygieldev.ecommercebackend.domain.CartId
import pl.szczygieldev.ecommercebackend.domain.Delivery
import pl.szczygieldev.ecommercebackend.domain.OrderId
import pl.szczygieldev.ecommercebackend.domain.OrderStatus

data class OrderProjection(
    val orderId: OrderId,
    val cartId: CartId,
    val status: OrderStatus,
    val paymentProjection: PaymentProjection,
    val delivery: Delivery
)