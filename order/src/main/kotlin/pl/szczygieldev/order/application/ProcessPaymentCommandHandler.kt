package pl.szczygieldev.order.application

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.ecommercelibrary.ddd.core.DomainEventPublisher
import pl.szczygieldev.order.application.port.`in`.command.ProcessPaymentCommand
import pl.szczygieldev.order.application.port.out.Orders
import pl.szczygieldev.order.application.port.out.PaymentService
import pl.szczygieldev.order.domain.event.OrderEvent

internal class ProcessPaymentCommandHandler(
    val orders: Orders,
    val orderEventPublisher: DomainEventPublisher<OrderEvent>,
    val paymentService: PaymentService,
) :
    CommandWithResultHandler<ProcessPaymentCommand, Either<ProcessPaymentCommand.Error, Unit>> {
    override suspend fun handle(command: ProcessPaymentCommand): Either<ProcessPaymentCommand.Error, Unit> = either {
        val paymentId = command.paymentId
        val paymentTransaction = command.paymentTransaction

        val order =
            orders.findByPaymentId(paymentId) ?: raise(ProcessPaymentCommand.OrderNotFoundError.forPaymentId(paymentId))

        val orderVersion = order.version
        order.pay(paymentTransaction)

        paymentService.verifyPayment(order.payment.id)

        val events = order.occurredEvents()
        orders.save(order, orderVersion)
        orderEventPublisher.publishBatch(events)
    }
}

