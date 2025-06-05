package pl.szczygieldev.order.application

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.ecommercelibrary.ddd.core.DomainEventPublisher
import pl.szczygieldev.order.application.port.`in`.command.AcceptOrderCommand
import pl.szczygieldev.order.application.port.out.Orders
import pl.szczygieldev.order.domain.error.AcceptError
import pl.szczygieldev.order.domain.event.OrderEvent

internal class AcceptOrderCommandHandler(
    val orderEventPublisher: DomainEventPublisher<OrderEvent>,
    val orders: Orders,
) : CommandWithResultHandler<AcceptOrderCommand, Either<AcceptOrderCommand.Error, Unit>> {

    override suspend fun handle(command: AcceptOrderCommand): Either<AcceptOrderCommand.Error, Unit> = either {
        val orderId = command.orderId
        val order = orders.findById(orderId) ?: raise(AcceptOrderCommand.OrderNotFoundError.forId(orderId))
        val orderVersion = order.version

        val result = order.accept()
        result.onLeft { error ->
            when (error) {
                is AcceptError.AlreadyAcceptedOrderError -> AcceptOrderCommand.AlreadyAcceptedOrderError.fromDomainError(
                    error
                )
            }
        }

        val events = order.occurredEvents()
        orders.save(order, orderVersion)
        orderEventPublisher.publishBatch(events)
    }
}