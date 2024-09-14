package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.presenter

import org.springframework.stereotype.Component
import pl.szczygieldev.ecommercebackend.application.model.OrderProjection
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.resource.*

@Component
class OrderPresenter {

    fun toDto(orderProjection: OrderProjection): OrderDto {
        val paymentProjection = orderProjection.paymentProjection
        val paymentDto =
            PaymentDto(
                paymentProjection.paymentId.id,
                paymentProjection.amount,
                paymentProjection.amountPaid,
                paymentProjection.paymentServiceProvider,
                paymentProjection.status,
                paymentProjection.paymentURL
            )
        val delivery = orderProjection.delivery
        val deliveryDto = DeliveryDto(delivery.deliveryProvider, delivery.status)

        return OrderDto(
            orderProjection.orderId.id(),
            orderProjection.cartId.id(),
            orderProjection.status,
            paymentDto,
            deliveryDto,
            orderProjection.createdAt,
            orderProjection.items.map { orderItemProjection ->
                OrderDto.OrderItemDto(
                    orderItemProjection.productId.id(),
                    orderItemProjection.title.value,
                    orderItemProjection.price.amount,
                    orderItemProjection.quantity
                )
            }
        )
    }

    fun toFullDto(orderProjection: OrderProjection): OrderFullDto {
        val paymentProjection = orderProjection.paymentProjection
        val paymentDto =
            PaymentFullDto(
                paymentProjection.paymentId.id,
                paymentProjection.amount,
                paymentProjection.amountPaid,
                paymentProjection.paymentServiceProvider,
                paymentProjection.status,
                paymentProjection.paymentURL,
                paymentProjection.transactions.map { paymentTransaction ->
                    PaymentFullDto.PaymentTransactionDto(
                        paymentTransaction.id.id,
                        paymentTransaction.amount,
                        paymentTransaction.timestamp
                    )
                }
            )
        val delivery = orderProjection.delivery
        val deliveryDto = DeliveryDto(delivery.deliveryProvider, delivery.status)

        return OrderFullDto(
            orderProjection.orderId.id(),
            orderProjection.cartId.id(),
            orderProjection.status,
            paymentDto,
            deliveryDto,
            orderProjection.createdAt, orderProjection.items.map { orderItemProjection ->
                OrderFullDto.OrderItemFullDto(
                    orderItemProjection.productId.id(),
                    orderItemProjection.title.value,
                    orderItemProjection.price.amount,
                    orderItemProjection.quantity
                )
            }
        )
    }
}