package pl.szczygieldev.order.application

import arrow.core.Either
import arrow.core.raise.either
import pl.szczygieldev.ecommercelibrary.architecture.UseCase
import pl.szczygieldev.ecommercelibrary.ddd.core.DomainEventPublisher
import pl.szczygieldev.order.application.port.`in`.OrderShippingUseCase
import pl.szczygieldev.order.application.port.`in`.command.BeginOrderPackingCommand
import pl.szczygieldev.order.application.port.`in`.command.ChangeOrderDeliveryStatusCommand
import pl.szczygieldev.order.application.port.`in`.command.CompleteOrderPackingCommand
import pl.szczygieldev.order.application.port.out.Orders
import pl.szczygieldev.order.application.port.out.OrdersProjections
import pl.szczygieldev.order.application.port.out.ShippingService
import pl.szczygieldev.order.domain.error.CompletePackingError.PackingNotInProgressError
import pl.szczygieldev.order.domain.error.PackingError
import pl.szczygieldev.order.domain.event.OrderEvent

@UseCase
internal class OrderShippingService(
    val orders: Orders,
    val orderEventPublisher: DomainEventPublisher<OrderEvent>,
    val shippingService: ShippingService,
    val ordersProjections: OrdersProjections,
) : OrderShippingUseCase {
    override suspend fun beginPacking(command: BeginOrderPackingCommand): Either<BeginOrderPackingCommand.Error, Unit> =
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

    override suspend fun completePacking(command: CompleteOrderPackingCommand): Either<CompleteOrderPackingCommand.Error, Unit> =
        either {
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


    override suspend fun changeDeliveryStatus(command: ChangeOrderDeliveryStatusCommand): Either<ChangeOrderDeliveryStatusCommand.Error, Unit> =
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