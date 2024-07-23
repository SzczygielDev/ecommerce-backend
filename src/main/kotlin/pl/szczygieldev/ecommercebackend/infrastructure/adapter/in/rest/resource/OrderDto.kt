package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.resource

import pl.szczygieldev.ecommercebackend.domain.*
data class OrderDto(
    val orderId: String,
    val cartId: String,
    val status: OrderStatus,
    val payment: PaymentDto,
    val delivery: DeliveryDto,
    val createdAt: String
)