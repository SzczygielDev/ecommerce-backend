package pl.szczygieldev.order.application

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.ecommercelibrary.ddd.core.DomainEventPublisher
import pl.szczygieldev.order.application.port.`in`.command.CancelOrderCommand
import pl.szczygieldev.order.application.port.out.Orders
import pl.szczygieldev.order.domain.error.CancelError
import pl.szczygieldev.order.domain.event.OrderEvent

internal class CancelOrderCommandHandler(
    val orderEventPublisher: DomainEventPublisher<OrderEvent>,
    val orders: Orders,
) : CommandWithResultHandler<CancelOrderCommand, Either<CancelOrderCommand.Error, Unit>> {
    override suspend fun handle(command: CancelOrderCommand): Either<CancelOrderCommand.Error, Unit> = either {
        val orderId = command.orderId
        val order = orders.findById(orderId) ?: raise(CancelOrderCommand.OrderNotFoundError.forId(orderId))
        val orderVersion = order.version

        val result = order.cancel()
        result.onLeft { error ->
            when (error) {
                is CancelError.CannotCancelSentOrderError -> CancelOrderCommand.CannotCancelSentOrderError.fromDomainError(
                    error
                )
            }
        }

        val events = order.occurredEvents()
        orders.save(order, orderVersion)
        orderEventPublisher.publishBatch(events)
    }
}