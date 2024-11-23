package pl.szczygieldev.order.application

import arrow.core.Either
import arrow.core.raise.either
import pl.szczygieldev.order.application.port.`in`.OrderPaymentUseCase
import pl.szczygieldev.order.application.port.`in`.command.ProcessPaymentCommand
import pl.szczygieldev.order.application.port.out.Orders
import pl.szczygieldev.order.application.port.out.OrdersProjections
import pl.szczygieldev.order.application.port.out.PaymentService
import pl.szczygieldev.order.domain.error.AppError
import pl.szczygieldev.order.domain.error.OrderNotFoundError
import pl.szczygieldev.order.domain.event.OrderEvent
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