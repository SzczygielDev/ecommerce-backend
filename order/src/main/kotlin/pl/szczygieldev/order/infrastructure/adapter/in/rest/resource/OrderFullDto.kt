package pl.szczygieldev.order.infrastructure.adapter.`in`.rest.resource

import pl.szczygieldev.order.domain.OrderStatus
import java.math.BigDecimal
import java.time.Instant

internal data class OrderFullDto(
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
        val quantity: Int,
        val imageId: String
    )
}