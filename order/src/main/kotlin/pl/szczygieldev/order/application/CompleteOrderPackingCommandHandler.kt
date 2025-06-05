package pl.szczygieldev.order.application

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.ecommercelibrary.ddd.core.DomainEventPublisher
import pl.szczygieldev.order.application.port.`in`.command.CompleteOrderPackingCommand
import pl.szczygieldev.order.application.port.out.Orders
import pl.szczygieldev.order.application.port.out.OrdersProjections
import pl.szczygieldev.order.application.port.out.ShippingService
import pl.szczygieldev.order.domain.error.CompletePackingError.PackingNotInProgressError
import pl.szczygieldev.order.domain.event.OrderEvent

internal class CompleteOrderPackingCommandHandler(
    val orders: Orders,
    val orderEventPublisher: DomainEventPublisher<OrderEvent>,
    val shippingService: ShippingService,
    val ordersProjections: OrdersProjections,
) : CommandWithResultHandler<CompleteOrderPackingCommand, Either<CompleteOrderPackingCommand.Error, Unit>> {
    override suspend fun handle(command: CompleteOrderPackingCommand): Either<CompleteOrderPackingCommand.Error, Unit> = either {
        val orderId = command.orderId
        val order = orders.findById(orderId) ?: raise(CompleteOrderPackingCommand.OrderNotFoundError.forId(orderId))
        val orderProjection = ordersProjections.findById(orderId) ?: raise(
            CompleteOrderPackingCommand.OrderNotFoundError.forId(orderId)
        )
        val orderVersion = order.version

        val parcelId =
            shippingService.registerParcel(command.dimensions, orderProjection.delivery.deliveryProvider) ?: raise(
                CompleteOrderPackingCommand.CannotRegisterParcelError.forId(orderId)
            )
        val result = order.completePacking(parcelId, command.dimensions)

        result.onLeft { error ->
            when (error) {
                is PackingNotInProgressError -> CompleteOrderPackingCommand.PackingNotInProgressError.fromDomainError(
                    error
                )
            }
        }

        val events = order.occurredEvents()
        orders.save(order, orderVersion)
        orderEventPublisher.publishBatch(events)
    }
}