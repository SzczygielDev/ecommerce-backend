package pl.szczygieldev.ecommercebackend.application

import arrow.core.Either
import arrow.core.raise.either
import pl.szczygieldev.ecommercebackend.application.port.`in`.OrderUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.*
import pl.szczygieldev.ecommercebackend.application.port.out.CartsProjections
import pl.szczygieldev.ecommercebackend.application.port.out.Orders
import pl.szczygieldev.ecommercebackend.domain.Order
import pl.szczygieldev.ecommercebackend.domain.error.AppError
import pl.szczygieldev.ecommercebackend.domain.error.CartNotFoundError
import pl.szczygieldev.ecommercebackend.domain.error.OrderNotFoundError
import pl.szczygieldev.ecommercebackend.domain.event.OrderEvent
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.messaging.OrderEventPublisher
import pl.szczygieldev.shared.architecture.UseCase
import pl.szczygieldev.shared.ddd.core.DomainEventPublisher

@UseCase
class OrderService(
    val cartProjections: CartsProjections,
    val orders: Orders,
    val orderEventPublisher: DomainEventPublisher<OrderEvent>
) : OrderUseCase {
    override fun createOrder(command: CreateOrderCommand): Either<AppError, Unit> = either {
        val cartId = command.cartId
        val cart = cartProjections.findById(cartId) ?: raise(CartNotFoundError.forId(cartId))

        val order = Order.create(
            orders.nextIdentity(),
            cart.cartId,
            cart.amount,
            command.paymentServiceProvider,
            command.deliveryProvider
        )
        val orderVersion = order.version
        orders.save(order,orderVersion)
        orderEventPublisher.publishBatch(order.occurredEvents())
    }

    override fun acceptOrder(command: AcceptOrderCommand): Either<AppError, Unit> = either {
        val orderId = command.orderId
        val order = orders.findById(orderId) ?: raise(OrderNotFoundError.forId(orderId))
        val orderVersion = order.version
        order.accept().bind()
        orders.save(order,orderVersion)
        orderEventPublisher.publishBatch(order.occurredEvents())
    }

    override fun rejectOrder(command: RejectOrderCommand): Either<AppError, Unit> = either {
        val orderId = command.orderId
        val order = orders.findById(orderId) ?: raise(OrderNotFoundError.forId(orderId))
        val orderVersion = order.version
        order.reject().bind()
        orders.save(order,orderVersion)
        orderEventPublisher.publishBatch(order.occurredEvents())
    }

    override fun cancelOrder(command: CancelOrderCommand): Either<AppError, Unit> = either {
        val orderId = command.orderId
        val order = orders.findById(orderId) ?: raise(OrderNotFoundError.forId(orderId))
        val orderVersion = order.version
        order.cancel().bind()
        orders.save(order,orderVersion)
        orderEventPublisher.publishBatch(order.occurredEvents())
    }

    override fun returnOrder(command: ReturnOrderCommand): Either<AppError, Unit> = either {
        val orderId = command.orderId
        val order = orders.findById(orderId) ?: raise(OrderNotFoundError.forId(orderId))
        val orderVersion = order.version
        order.returnOrder().bind()
        orders.save(order,orderVersion)
        orderEventPublisher.publishBatch(order.occurredEvents())
    }

}