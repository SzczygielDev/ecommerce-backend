package pl.szczygieldev.order.application

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.ecommercelibrary.ddd.core.DomainEventPublisher
import pl.szczygieldev.order.application.port.`in`.command.ChangeOrderDeliveryStatusCommand
import pl.szczygieldev.order.application.port.out.Orders
import pl.szczygieldev.order.application.port.out.OrdersProjections
import pl.szczygieldev.order.domain.event.OrderEvent

internal class ChangeOrderDeliveryStatusCommandHandler(
    val orders: Orders,
    val orderEventPublisher: DomainEventPublisher<OrderEvent>,
    val ordersProjections: OrdersProjections,
) : CommandWithResultHandler<ChangeOrderDeliveryStatusCommand, Either<ChangeOrderDeliveryStatusCommand.Error, Unit>> {
    override suspend fun handle(command: ChangeOrderDeliveryStatusCommand): Either<ChangeOrderDeliveryStatusCommand.Error, Unit> =
        either {
            val parcelIdentifier = command.parcelId
            val orderProjection = ordersProjections.findByParcelIdentifier(parcelIdentifier) ?: raise(
                ChangeOrderDeliveryStatusCommand.OrderNotFoundError.forParcelId(parcelIdentifier)
            )

            val orderId = orderProjection.orderId
            val order =
                orders.findById(orderId) ?: raise(ChangeOrderDeliveryStatusCommand.OrderNotFoundError.forId(orderId))
            val orderVersion = order.version

            order.changeDeliveryStatus(command.status)

            val events = order.occurredEvents()
            orders.save(order, orderVersion)
            orderEventPublisher.publishBatch(events)
        }
}