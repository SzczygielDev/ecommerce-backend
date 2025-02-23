package pl.szczygieldev.order.infrastructure.adapter.`in`.readmodel

import arrow.core.raise.either
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import pl.szczygieldev.ecommercelibrary.ddd.core.DomainEventHandler
import pl.szczygieldev.order.application.port.`in`.query.model.OrderProjection
import pl.szczygieldev.order.application.port.`in`.query.model.PaymentProjection
import pl.szczygieldev.order.application.port.out.OrdersProjections
import pl.szczygieldev.order.application.port.out.Products
import pl.szczygieldev.order.domain.*
import pl.szczygieldev.order.domain.error.AppError
import pl.szczygieldev.order.domain.error.OrderNotFoundError
import pl.szczygieldev.order.domain.error.ProductNotFoundError
import pl.szczygieldev.order.domain.event.*
import java.math.BigDecimal

@Component
internal class OrderReadModelEventHandler(
    val ordersProjections: OrdersProjections,
    val products: Products
) :
    DomainEventHandler<OrderEvent> {
    companion object {
        private val log = KotlinLogging.logger { }
    }

    @EventListener
    override suspend fun handleEvent(domainEvent: OrderEvent) = either<AppError, Unit> {
        when (domainEvent) {
            is OrderCreated -> {
                val paymentDetails = domainEvent.paymentDetails

                val orderItemsProjections = domainEvent.items.map { orderItem ->

                    val productId = orderItem.productId
                    val product = products.findById(productId) ?: raise(ProductNotFoundError.forId(productId))

                    OrderProjection.OrderItemProjection(
                        orderItem.productId,
                        product.title,
                        product.price,
                        orderItem.quantity,
                        product.imageId
                    )

                }.toList()

                ordersProjections.save(
                    OrderProjection(
                        domainEvent.orderId,
                        domainEvent.cartId,
                        OrderStatus.CREATED,
                        PaymentProjection(
                            paymentDetails.id,
                            paymentDetails.amount,
                            BigDecimal.ZERO,
                            paymentDetails.paymentServiceProvider,
                            PaymentStatus.UNPAID,
                            paymentDetails.url, emptyList()
                        ),
                        Delivery(domainEvent.deliveryProvider, DeliveryStatus.WAITING, null),
                        domainEvent.occurredOn,
                        orderItemsProjections
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


            is OrderPackaged -> {
                val orderId = domainEvent.orderId
                val foundOrder = ordersProjections.findById(orderId) ?: raise(OrderNotFoundError.forId(orderId))
                ordersProjections.save(
                    foundOrder.copy(
                        status = OrderStatus.READY, delivery = foundOrder.delivery.copy(
                            parcel = Parcel(
                                domainEvent.parcelId, domainEvent.parcelDimensions,
                            )
                        )
                    )
                )
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


            is OrderPaymentReceived -> {
                val orderId = domainEvent.orderId
                val foundOrder = ordersProjections.findById(orderId) ?: raise(OrderNotFoundError.forId(orderId))
                val paymentProjection = foundOrder.paymentProjection

                val transactions = mutableListOf<PaymentTransaction>()
                transactions.addAll(paymentProjection.transactions)
                transactions.add(domainEvent.paymentTransaction)

                val updatedPaymentProjection =
                    paymentProjection.copy(
                        amountPaid = paymentProjection.amountPaid.add(domainEvent.paymentTransaction.amount),
                        transactions = transactions
                    )
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

            is OrderDeliveryStatusChanged -> {
                val orderId = domainEvent.orderId
                val foundOrder = ordersProjections.findById(orderId) ?: raise(OrderNotFoundError.forId(orderId))

                var updatedOrder = foundOrder.copy(delivery = foundOrder.delivery.copy(status = domainEvent.status))
                if (domainEvent.status != DeliveryStatus.WAITING) {
                    updatedOrder = updatedOrder.copy(status = OrderStatus.SENT)
                }

                ordersProjections.save(updatedOrder)
            }
        }
    }.fold({
        log.error { "Event handling failed=${domainEvent}" }
    }, {
        log.info { "Event handled=${domainEvent}" }
    })
}