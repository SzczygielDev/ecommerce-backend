package pl.szczygieldev.order.application

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.ecommercelibrary.ddd.core.DomainEventPublisher
import pl.szczygieldev.order.application.port.`in`.command.RejectOrderCommand
import pl.szczygieldev.order.application.port.out.Orders
import pl.szczygieldev.order.domain.error.RejectError
import pl.szczygieldev.order.domain.event.OrderEvent

internal class RejectOrderCommandHandler(
    val orderEventPublisher: DomainEventPublisher<OrderEvent>,
    val orders: Orders,
) : CommandWithResultHandler<RejectOrderCommand, Either<RejectOrderCommand.Error, Unit>> {
    override suspend fun handle(command: RejectOrderCommand): Either<RejectOrderCommand.Error, Unit> = either {
        val orderId = command.orderId
        val order = orders.findById(orderId) ?: raise(RejectOrderCommand.OrderNotFoundError.forId(orderId))
        val orderVersion = order.version

        val result = order.reject()
        result.onLeft { error ->
            when (error) {
                is RejectError.AlreadyAcceptedOrderError -> RejectOrderCommand.AlreadyAcceptedOrderError.fromDomainError(
                    error
                )
            }
        }

        val events = order.occurredEvents()
        orders.save(order, orderVersion)
        orderEventPublisher.publishBatch(events)
    }
}