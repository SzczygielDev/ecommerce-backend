package pl.szczygieldev.order.infrastructure.adapter.`in`.rest.resource

import pl.szczygieldev.order.domain.*
import java.math.BigDecimal
import java.time.Instant

internal data class OrderDto(
    val orderId: String,
    val cartId: String,
    val status: OrderStatus,
    val payment: PaymentDto,
    val delivery: DeliveryDto,
    val createdAt: Instant,
    val items: List<OrderItemDto>
) {
    data class OrderItemDto(
        val productId: String,
        var title: String,
        val price: BigDecimal,
        val quantity: Int,
        val imageId: String
    )
}