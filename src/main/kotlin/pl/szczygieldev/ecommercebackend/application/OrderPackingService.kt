package pl.szczygieldev.ecommercebackend.application

import arrow.core.Either
import arrow.core.raise.either
import pl.szczygieldev.ecommercebackend.application.port.`in`.OrderPackingUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.BeginOrderPackingCommand
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.ChangeOrderDeliveryStatusCommand
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.CompleteOrderPackingCommand
import pl.szczygieldev.ecommercebackend.application.port.out.Orders
import pl.szczygieldev.ecommercebackend.application.port.out.OrdersProjections
import pl.szczygieldev.ecommercebackend.application.port.out.ShippingService
import pl.szczygieldev.ecommercebackend.domain.error.AppError
import pl.szczygieldev.ecommercebackend.domain.error.CannotRegisterParcelError
import pl.szczygieldev.ecommercebackend.domain.error.OrderNotFoundError
import pl.szczygieldev.ecommercebackend.domain.event.OrderEvent
import pl.szczygieldev.shared.architecture.UseCase
import pl.szczygieldev.shared.ddd.core.DomainEventPublisher

@UseCase
class OrderPackingService(
    val orders: Orders,
    val orderEventPublisher: DomainEventPublisher<OrderEvent>,
    val shippingService: ShippingService,
    val ordersProjections: OrdersProjections,
) : OrderPackingUseCase {
    override fun beginPacking(command: BeginOrderPackingCommand): Either<AppError, Unit> = either {
        val orderId = command.orderId
        val order = orders.findById(orderId) ?: raise(OrderNotFoundError.forId(orderId))
        val orderVersion = order.version

        order.beginPacking().bind()

        val events = order.occurredEvents()
        orders.save(order, orderVersion)
        orderEventPublisher.publishBatch(events)
    }

    override fun completePacking(command: CompleteOrderPackingCommand): Either<AppError, Unit> = either {
        val orderId = command.orderId
        val order = orders.findById(orderId) ?: raise(OrderNotFoundError.forId(orderId))
        val orderVersion = order.version

        val parcelIdentifier = shippingService.registerParcel(command.dimensions) ?: raise(CannotRegisterParcelError.forId(orderId))
        order.completePacking(parcelIdentifier,command.dimensions).bind()

        val events = order.occurredEvents()
        orders.save(order, orderVersion)
        orderEventPublisher.publishBatch(events)
    }



    override fun changeDeliveryStatus(command: ChangeOrderDeliveryStatusCommand): Either<AppError, Unit> = either {
        val parcelIdentifier = command.parcelIdentifier
        val orderProjection = ordersProjections.findByParcelIdentifier(parcelIdentifier) ?: raise(OrderNotFoundError.forParcelIdentifier(parcelIdentifier))

        val orderId = orderProjection.orderId
        val order = orders.findById(orderId) ?: raise(OrderNotFoundError.forId(orderId))
        val orderVersion = order.version

        order.changeDeliveryStatus(command.status)

        val events = order.occurredEvents()
        orders.save(order, orderVersion)
        orderEventPublisher.publishBatch(events)
    }
}