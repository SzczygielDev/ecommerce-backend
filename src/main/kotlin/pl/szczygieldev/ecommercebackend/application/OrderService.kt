package pl.szczygieldev.ecommercebackend.application

import arrow.core.Either
import arrow.core.raise.either
import pl.szczygieldev.ecommercebackend.application.handlers.*
import pl.szczygieldev.ecommercebackend.application.port.`in`.OrderUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.*
import pl.szczygieldev.ecommercebackend.application.port.out.CartsProjections
import pl.szczygieldev.ecommercebackend.application.port.out.Orders
import pl.szczygieldev.ecommercebackend.application.port.out.PaymentService
import pl.szczygieldev.ecommercebackend.domain.Order
import pl.szczygieldev.ecommercebackend.domain.PaymentDetails
import pl.szczygieldev.ecommercebackend.domain.error.AppError
import pl.szczygieldev.ecommercebackend.domain.error.CartNotFoundError
import pl.szczygieldev.ecommercebackend.domain.event.OrderEvent
import pl.szczygieldev.shared.architecture.UseCase
import pl.szczygieldev.shared.ddd.core.DomainEventPublisher
import java.net.URL

@UseCase
class OrderService(
    val orders: Orders,
    val cartProjections: CartsProjections,
    val paymentService: PaymentService,
    val orderEventPublisher: DomainEventPublisher<OrderEvent>,
    val acceptOrderCommandHandler: AcceptOrderCommandHandler,
    val rejectOrderCommandHandler: RejectOrderCommandHandler,
    val cancelOrderCommandHandler: CancelOrderCommandHandler,
    val returnOrderCommandHandler: ReturnOrderCommandHandler,
) : OrderUseCase {
    private val paymentReturnUrlBase = "http://localhost:64427/paymentResult/"
    override suspend fun createOrder(command: CreateOrderCommand): Either<AppError, Unit> = either {
        val cartId = command.cartId
        val cart = cartProjections.findById(cartId) ?: raise(CartNotFoundError.forId(cartId))
        val paymentServiceProvider = command.paymentServiceProvider

        val orderId = orders.nextIdentity()
        val paymentRegistration = paymentService.registerPayment(cart.amount, paymentServiceProvider, URL("$paymentReturnUrlBase${orderId.id()}"))

        val order = Order.create(
            orderId,
            cart.cartId,
            cart.amount,
            PaymentDetails(
                paymentRegistration.id,
                cart.amount,
                paymentRegistration.url,
                paymentServiceProvider
            ),
            command.deliveryProvider
        )

        val orderVersion = order.version
        val events = order.occurredEvents()
        orders.save(order, orderVersion)
        orderEventPublisher.publishBatch(events)
    }

    override suspend fun acceptOrder(command: AcceptOrderCommand): Either<AppError, Unit> = either {
        acceptOrderCommandHandler.executeInBackground(command).bind()
    }

    override suspend fun rejectOrder(command: RejectOrderCommand): Either<AppError, Unit> = either {
        rejectOrderCommandHandler.executeInBackground(command).bind()
    }

    override suspend fun cancelOrder(command: CancelOrderCommand): Either<AppError, Unit> = either {
        cancelOrderCommandHandler.executeInBackground(command).bind()
    }

    override suspend fun returnOrder(command: ReturnOrderCommand): Either<AppError, Unit> = either {
        returnOrderCommandHandler.executeInBackground(command).bind()
    }
}