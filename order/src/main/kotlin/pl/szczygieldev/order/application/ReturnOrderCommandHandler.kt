package pl.szczygieldev.order.application

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.ecommercelibrary.ddd.core.DomainEventPublisher
import pl.szczygieldev.order.application.port.`in`.command.ReturnOrderCommand
import pl.szczygieldev.order.application.port.out.Orders
import pl.szczygieldev.order.domain.error.ReturnError
import pl.szczygieldev.order.domain.event.OrderEvent

internal class ReturnOrderCommandHandler(
    val orderEventPublisher: DomainEventPublisher<OrderEvent>,
    val orders: Orders,
) : CommandWithResultHandler<ReturnOrderCommand, Either<ReturnOrderCommand.Error, Unit>> {

    override suspend fun handle(command: ReturnOrderCommand): Either<ReturnOrderCommand.Error, Unit> = either {
        val orderId = command.orderId
        val order = orders.findById(orderId) ?: raise(ReturnOrderCommand.OrderNotFoundError.forId(orderId))
        val orderVersion = order.version

        val result = order.returnOrder()
        result.onLeft { error ->
            when (error) {
                is ReturnError.CannotReturnNotReceivedOrderError -> ReturnOrderCommand.CannotReturnNotReceivedOrderError.fromDomainError(
                    error
                )
            }
        }

        val events = order.occurredEvents()
        orders.save(order, orderVersion)
        orderEventPublisher.publishBatch(events)
    }
}