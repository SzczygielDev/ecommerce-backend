package pl.szczygieldev.ecommercebackend.application.handlers

import arrow.core.Either
import arrow.core.raise.either
import pl.szczygieldev.ecommercebackend.application.handlers.common.CommandHandler
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.ChangeOrderDeliveryStatusCommand
import pl.szczygieldev.ecommercebackend.application.port.out.CommandResultStorage
import pl.szczygieldev.ecommercebackend.application.port.out.Orders
import pl.szczygieldev.ecommercebackend.application.port.out.OrdersProjections
import pl.szczygieldev.ecommercebackend.domain.error.AppError
import pl.szczygieldev.ecommercebackend.domain.error.OrderNotFoundError
import pl.szczygieldev.ecommercebackend.domain.event.OrderEvent
import pl.szczygieldev.shared.ddd.core.DomainEventPublisher

class ChangeOrderDeliveryStatusCommandHandler(
    val orders: Orders,
    val orderEventPublisher: DomainEventPublisher<OrderEvent>,
    val ordersProjections: OrdersProjections,
    commandResultStorage: CommandResultStorage
) : CommandHandler<ChangeOrderDeliveryStatusCommand>(commandResultStorage) {
    override suspend fun processCommand(command: ChangeOrderDeliveryStatusCommand): Either<AppError, Unit> = either {
        val parcelIdentifier = command.parcelId
        val orderProjection = ordersProjections.findByParcelIdentifier(parcelIdentifier) ?: raise(
            OrderNotFoundError.forParcelId(parcelIdentifier)
        )

        val orderId = orderProjection.orderId
        val order = orders.findById(orderId) ?: raise(OrderNotFoundError.forId(orderId))
        val orderVersion = order.version

        order.changeDeliveryStatus(command.status)

        val events = order.occurredEvents()
        orders.save(order, orderVersion)
        orderEventPublisher.publishBatch(events)
    }
}