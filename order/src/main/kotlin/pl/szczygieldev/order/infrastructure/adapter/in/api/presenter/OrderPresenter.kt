package pl.szczygieldev.order.infrastructure.adapter.`in`.api.presenter

import org.springframework.stereotype.Component
import pl.szczygieldev.order.application.port.`in`.query.model.OrderProjection
import pl.szczygieldev.order.infrastructure.adapter.`in`.api.resource.*

@Component
internal class OrderPresenter {

    fun toDto(orderProjection: OrderProjection): OrderDto {
        val paymentProjection = orderProjection.paymentProjection
        val paymentDto =
            PaymentDto(
                paymentProjection.paymentId.id.toString(),
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
                    orderItemProjection.title,
                    orderItemProjection.price,
                    orderItemProjection.quantity,
                    orderItemProjection.imageId.id()
                )
            }
        )
    }

    fun toFullDto(orderProjection: OrderProjection): OrderFullDto {
        val paymentProjection = orderProjection.paymentProjection
        val paymentDto =
            PaymentFullDto(
                paymentProjection.paymentId.id.toString(),
                paymentProjection.amount,
                paymentProjection.amountPaid,
                paymentProjection.paymentServiceProvider,
                paymentProjection.status,
                paymentProjection.paymentURL,
                paymentProjection.transactions.map { paymentTransaction ->
                    PaymentFullDto.PaymentTransactionDto(
                        paymentTransaction.id.id.toString(),
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
                    orderItemProjection.title,
                    orderItemProjection.price,
                    orderItemProjection.quantity,
                    orderItemProjection.imageId.id()
                )
            }
        )
    }
}