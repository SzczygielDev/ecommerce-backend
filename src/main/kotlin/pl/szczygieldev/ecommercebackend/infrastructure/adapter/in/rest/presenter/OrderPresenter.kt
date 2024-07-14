package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.presenter

import org.springframework.stereotype.Component
import pl.szczygieldev.ecommercebackend.application.model.OrderProjection
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.resource.DeliveryDto
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.resource.OrderDto
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.resource.PaymentDto

@Component
class OrderPresenter {

    fun toDto(orderProjection: OrderProjection): OrderDto {
        val paymentProjection = orderProjection.paymentProjection
        val paymentDto =
            PaymentDto(paymentProjection.amount, paymentProjection.amountPaid, paymentProjection.paymentServiceProvider, paymentProjection.status)
        val delivery = orderProjection.delivery
        val deliveryDto = DeliveryDto(delivery.deliveryProvider, delivery.status)

        return OrderDto(
            orderProjection.orderId.id(),
            orderProjection.cartId.id(),
            orderProjection.status,
            paymentDto,
            deliveryDto
        )
    }
}