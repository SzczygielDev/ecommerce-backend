package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.resource

import pl.szczygieldev.ecommercebackend.domain.OrderStatus

data class OrderFullDto(
    val orderId: String,
    val cartId: String,
    val status: OrderStatus,
    val payment: PaymentFullDto,
    val delivery: DeliveryDto
)