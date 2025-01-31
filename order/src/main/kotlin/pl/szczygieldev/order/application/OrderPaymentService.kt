package pl.szczygieldev.order.application

import arrow.core.Either
import arrow.core.raise.either
import pl.szczygieldev.ecommercelibrary.architecture.UseCase
import pl.szczygieldev.ecommercelibrary.ddd.core.DomainEventPublisher
import pl.szczygieldev.order.application.port.`in`.OrderPaymentUseCase
import pl.szczygieldev.order.application.port.`in`.command.ProcessPaymentCommand
import pl.szczygieldev.order.application.port.out.Orders
import pl.szczygieldev.order.application.port.out.PaymentService
import pl.szczygieldev.order.domain.error.AppError
import pl.szczygieldev.order.domain.error.OrderNotFoundError
import pl.szczygieldev.order.domain.event.OrderEvent

@UseCase
internal class OrderPaymentService(
    val orders: Orders,
    val orderEventPublisher: DomainEventPublisher<OrderEvent>,
    val paymentService: PaymentService,
) : OrderPaymentUseCase {
    override fun pay(command: ProcessPaymentCommand): Either<AppError, Unit> = either {
        val paymentId = command.paymentId
        val paymentTransaction = command.paymentTransaction

        val order = orders.findByPaymentId(paymentId) ?: raise(OrderNotFoundError.forPaymentId(paymentId))

        val orderVersion = order.version
        order.pay(paymentTransaction)

        paymentService.verifyPayment(order.payment.id)

        val events = order.occurredEvents()
        orders.save(order, orderVersion)
        orderEventPublisher.publishBatch(events)
    }
}