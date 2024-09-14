package pl.szczygieldev.ecommercebackend.application.handlers

import arrow.core.Either
import arrow.core.raise.either
import pl.szczygieldev.ecommercebackend.application.handlers.common.CommandHandler
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.BeginOrderPackingCommand
import pl.szczygieldev.ecommercebackend.application.port.out.CommandResultStorage
import pl.szczygieldev.ecommercebackend.application.port.out.Orders
import pl.szczygieldev.ecommercebackend.domain.error.AppError
import pl.szczygieldev.ecommercebackend.domain.error.OrderNotFoundError
import pl.szczygieldev.ecommercebackend.domain.event.OrderEvent
import pl.szczygieldev.shared.ddd.core.DomainEventPublisher

class BeginOrderPackingCommandHandler(
    val orders: Orders,
    val orderEventPublisher: DomainEventPublisher<OrderEvent>,
    commandResultStorage: CommandResultStorage
) : CommandHandler<BeginOrderPackingCommand>(commandResultStorage) {
    override suspend fun processCommand(command: BeginOrderPackingCommand): Either<AppError, Unit> = either{
        val orderId = command.orderId
        val order = orders.findById(orderId) ?: raise(OrderNotFoundError.forId(orderId))
        val orderVersion = order.version

        order.beginPacking().bind()

        val events = order.occurredEvents()
        orders.save(order, orderVersion)
        orderEventPublisher.publishBatch(events)
    }
}