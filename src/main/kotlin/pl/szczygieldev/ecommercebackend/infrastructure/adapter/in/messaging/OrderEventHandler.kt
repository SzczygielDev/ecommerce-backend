package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.messaging

import arrow.core.raise.either
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import pl.szczygieldev.ecommercebackend.application.model.OrderProjection
import pl.szczygieldev.ecommercebackend.application.model.PaymentProjection
import pl.szczygieldev.ecommercebackend.application.port.out.OrdersProjections
import pl.szczygieldev.ecommercebackend.domain.Delivery
import pl.szczygieldev.ecommercebackend.domain.DeliveryStatus
import pl.szczygieldev.ecommercebackend.domain.OrderStatus
import pl.szczygieldev.ecommercebackend.domain.PaymentStatus
import pl.szczygieldev.ecommercebackend.domain.error.AppError
import pl.szczygieldev.ecommercebackend.domain.error.OrderNotFoundError
import pl.szczygieldev.ecommercebackend.domain.event.*
import pl.szczygieldev.shared.ddd.core.DomainEventHandler

@Component
class OrderEventHandler(val ordersProjections: OrdersProjections) : DomainEventHandler<OrderEvent> {
    companion object {
        private val log = KotlinLogging.logger { }
    }

    @EventListener
    override fun handleEvent(domainEvent: OrderEvent) = either<AppError, Unit> {
        when (domainEvent) {
            is OrderCreated -> {
                ordersProjections.save(
                    OrderProjection(
                        domainEvent.orderId,
                        domainEvent.cartId,
                        OrderStatus.CREATED,
                        PaymentProjection(domainEvent.amount, domainEvent.paymentServiceProvider, PaymentStatus.UNPAID),
                        Delivery(domainEvent.deliveryProvider, null, DeliveryStatus.WAITING)
                    )
                )
            }

            is OrderAccepted -> {
                val orderId = domainEvent.orderId
                val foundOrder = ordersProjections.findById(orderId) ?: raise(OrderNotFoundError.forId(orderId))
                ordersProjections.save(foundOrder.copy(status = OrderStatus.ACCEPTED))
            }

            is OrderCanceled -> {
                val orderId = domainEvent.orderId
                val foundOrder = ordersProjections.findById(orderId) ?: raise(OrderNotFoundError.forId(orderId))
                ordersProjections.save(foundOrder.copy(status = OrderStatus.CANCELLED))
            }

            is OrderDelivered -> {
                val orderId = domainEvent.orderId
                val foundOrder = ordersProjections.findById(orderId) ?: raise(OrderNotFoundError.forId(orderId))
                ordersProjections.save(foundOrder.copy(delivery = foundOrder.delivery.copy(status = DeliveryStatus.DELIVERED)))
            }

            is OrderPackaged -> {
                val orderId = domainEvent.orderId
                val foundOrder = ordersProjections.findById(orderId) ?: raise(OrderNotFoundError.forId(orderId))
                ordersProjections.save(foundOrder.copy(status = OrderStatus.READY))
            }

            is OrderPackagingStarted -> {
                val orderId = domainEvent.orderId
                val foundOrder = ordersProjections.findById(orderId) ?: raise(OrderNotFoundError.forId(orderId))
                ordersProjections.save(foundOrder.copy(status = OrderStatus.IN_PROGRESS))
            }

            is OrderRejected -> {
                val orderId = domainEvent.orderId
                val foundOrder = ordersProjections.findById(orderId) ?: raise(OrderNotFoundError.forId(orderId))
                ordersProjections.save(foundOrder.copy(status = OrderStatus.REJECTED))
            }

            is OrderSent -> {
                val orderId = domainEvent.orderId
                val foundOrder = ordersProjections.findById(orderId) ?: raise(OrderNotFoundError.forId(orderId))
                ordersProjections.save(foundOrder.copy(status = OrderStatus.IN_PROGRESS))
            }

            is OrderPaymentReceived -> {
                val orderId = domainEvent.orderId
                val foundOrder = ordersProjections.findById(orderId) ?: raise(OrderNotFoundError.forId(orderId))
                val paymentProjection = foundOrder.paymentProjection
                val updatedPaymentProjection =
                    paymentProjection.copy(amount = paymentProjection.amount.add(domainEvent.paymentTransaction.amount))
                ordersProjections.save(foundOrder.copy(paymentProjection = updatedPaymentProjection))
            }

            is OrderInvalidAmountPaid -> {
                val orderId = domainEvent.orderId
                val foundOrder = ordersProjections.findById(orderId) ?: raise(OrderNotFoundError.forId(orderId))
                ordersProjections.save(foundOrder.copy(paymentProjection = foundOrder.paymentProjection.copy(status = PaymentStatus.INVALID_AMOUNT)))
            }

            is OrderPaid -> {
                val orderId = domainEvent.orderId
                val foundOrder = ordersProjections.findById(orderId) ?: raise(OrderNotFoundError.forId(orderId))
                ordersProjections.save(foundOrder.copy(paymentProjection = foundOrder.paymentProjection.copy(status = PaymentStatus.PAID)))
            }
        }
    }.fold({
        log.error { "Event handling failed=${domainEvent}" }
    }, {
        log.info { "Event handled=${domainEvent}" }
    })
}