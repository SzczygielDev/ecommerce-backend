package pl.szczygieldev.ecommercebackend.application.handlers

import arrow.core.Either
import arrow.core.raise.either
import pl.szczygieldev.ecommercebackend.application.handlers.common.CommandHandler
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.CompleteOrderPackingCommand
import pl.szczygieldev.ecommercebackend.application.port.out.CommandResultStorage
import pl.szczygieldev.ecommercebackend.application.port.out.Orders
import pl.szczygieldev.ecommercebackend.application.port.out.OrdersProjections
import pl.szczygieldev.ecommercebackend.application.port.out.ShippingService
import pl.szczygieldev.ecommercebackend.domain.DeliveryProvider
import pl.szczygieldev.ecommercebackend.domain.OrderId
import pl.szczygieldev.ecommercebackend.domain.ParcelDimensions
import pl.szczygieldev.ecommercebackend.domain.ParcelIdentifier
import pl.szczygieldev.ecommercebackend.domain.error.AppError
import pl.szczygieldev.ecommercebackend.domain.error.CannotRegisterParcelError
import pl.szczygieldev.ecommercebackend.domain.error.OrderNotFoundError
import pl.szczygieldev.ecommercebackend.domain.event.OrderEvent
import pl.szczygieldev.shared.ddd.core.DomainEventPublisher

class CompleteOrderPackingCommandHandler(
    val orders: Orders,
    val orderEventPublisher: DomainEventPublisher<OrderEvent>,
    val shippingService: ShippingService,
    val ordersProjections: OrdersProjections,
    commandResultStorage: CommandResultStorage
) : CommandHandler<CompleteOrderPackingCommand>(commandResultStorage) {
    override suspend fun processCommand(command: CompleteOrderPackingCommand): Either<AppError, Unit> = either{
        val orderId = command.orderId
        val order = orders.findById(orderId) ?: raise(OrderNotFoundError.forId(orderId))
        val orderProjection = ordersProjections.findById(orderId) ?: raise(
            OrderNotFoundError.forId(orderId)
        )
        val orderVersion = order.version

        val parcelIdentifier = sendOrder(orderId, orderProjection.delivery.deliveryProvider, command.dimensions).bind()
        order.completePacking(parcelIdentifier, command.dimensions).bind()

        val events = order.occurredEvents()
        orders.save(order, orderVersion)
        orderEventPublisher.publishBatch(events)
    }

    private fun sendOrder(
        orderId: OrderId,
        provider: DeliveryProvider,
        parcelDimensions: ParcelDimensions
    ): Either<AppError, ParcelIdentifier> =
        either {
            return@either when (provider) {
                DeliveryProvider.MOCK_DELIVERY_SERVICE -> shippingService.registerParcel(parcelDimensions) ?: raise(
                    CannotRegisterParcelError.forId(orderId)
                )
            }
        }
}