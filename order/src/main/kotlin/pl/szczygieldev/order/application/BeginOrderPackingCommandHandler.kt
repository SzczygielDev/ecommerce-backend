package pl.szczygieldev.order.application

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.ecommercelibrary.ddd.core.DomainEventPublisher
import pl.szczygieldev.order.application.port.`in`.command.BeginOrderPackingCommand
import pl.szczygieldev.order.application.port.out.Orders
import pl.szczygieldev.order.domain.error.PackingError
import pl.szczygieldev.order.domain.event.OrderEvent

internal class BeginOrderPackingCommandHandler(
    val orders: Orders,
    val orderEventPublisher: DomainEventPublisher<OrderEvent>
) :
    CommandWithResultHandler<BeginOrderPackingCommand, Either<BeginOrderPackingCommand.Error, Unit>> {
    override suspend fun handle(command: BeginOrderPackingCommand): Either<BeginOrderPackingCommand.Error, Unit> =
        either {
            val orderId = command.orderId
            val order = orders.findById(orderId) ?: raise(BeginOrderPackingCommand.OrderNotFoundError.forId(orderId))
            val orderVersion = order.version

            val result = order.beginPacking()
            result.onLeft { error ->
                when (error) {
                    is PackingError.CannotPackageNotAcceptedOrderError -> BeginOrderPackingCommand.CannotPackageNotAcceptedOrderError.fromDomainError(
                        error
                    )

                    is PackingError.NotPaidOrderError -> BeginOrderPackingCommand.NotPaidOrderError.fromDomainError(
                        error
                    )
                }
            }

            val events = order.occurredEvents()
            orders.save(order, orderVersion)
            orderEventPublisher.publishBatch(events)
        }
}