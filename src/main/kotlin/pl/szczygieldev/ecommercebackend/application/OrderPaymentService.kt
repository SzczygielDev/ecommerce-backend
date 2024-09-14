package pl.szczygieldev.ecommercebackend.application

import arrow.core.Either
import arrow.core.raise.either
import pl.szczygieldev.ecommercebackend.application.port.`in`.OrderPaymentUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.ProcessPaymentCommand
import pl.szczygieldev.ecommercebackend.application.port.out.Orders
import pl.szczygieldev.ecommercebackend.application.port.out.OrdersProjections
import pl.szczygieldev.ecommercebackend.application.port.out.PaymentService
import pl.szczygieldev.ecommercebackend.domain.error.AppError
import pl.szczygieldev.ecommercebackend.domain.error.OrderNotFoundError
import pl.szczygieldev.ecommercebackend.domain.event.OrderEvent
import pl.szczygieldev.shared.architecture.UseCase
import pl.szczygieldev.shared.ddd.core.DomainEventPublisher

@UseCase
class OrderPaymentService(
    val ordersProjections: OrdersProjections,
    val orders: Orders,
    val orderEventPublisher: DomainEventPublisher<OrderEvent>,
    val paymentService: PaymentService,
) : OrderPaymentUseCase {
    override fun pay(command: ProcessPaymentCommand): Either<AppError, Unit> = either {
        val paymentId = command.paymentId
        val paymentTransaction = command.paymentTransaction

        var orderProjection =
            ordersProjections.findByPaymentId(paymentId) ?: raise(OrderNotFoundError.forPaymentId(paymentId))
        val order = orders.findById(orderProjection.orderId) ?: raise(OrderNotFoundError.forPaymentId(paymentId))

        val orderVersion = order.version
        order.pay(paymentTransaction)

        paymentService.verifyPayment(orderProjection.paymentProjection.paymentId)

        val events = order.occurredEvents()
        orders.save(order, orderVersion)
        orderEventPublisher.publishBatch(events)
    }
}