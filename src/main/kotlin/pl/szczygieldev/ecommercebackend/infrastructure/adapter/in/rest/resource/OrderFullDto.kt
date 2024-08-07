package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.resource

import pl.szczygieldev.ecommercebackend.domain.OrderStatus
import java.math.BigDecimal
import java.time.Instant

data class OrderFullDto(
    val orderId: String,
    val cartId: String,
    val status: OrderStatus,
    val payment: PaymentFullDto,
    val delivery: DeliveryDto,
    val createdAt: Instant,
    val items: List<OrderItemFullDto>
) {
    data class OrderItemFullDto(
        val productId: String,
        var title: String,
        val price: BigDecimal,
        val quantity: Int
    )
}