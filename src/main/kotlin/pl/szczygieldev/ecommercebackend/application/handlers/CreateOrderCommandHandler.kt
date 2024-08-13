package pl.szczygieldev.ecommercebackend.application.handlers

import arrow.core.Either
import arrow.core.raise.either
import pl.szczygieldev.ecommercebackend.application.handlers.common.CommandHandler
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.CreateOrderCommand
import pl.szczygieldev.ecommercebackend.application.port.out.CartsProjections
import pl.szczygieldev.ecommercebackend.application.port.out.CommandResultStorage
import pl.szczygieldev.ecommercebackend.application.port.out.Orders
import pl.szczygieldev.ecommercebackend.application.port.out.PaymentService
import pl.szczygieldev.ecommercebackend.domain.Order
import pl.szczygieldev.ecommercebackend.domain.PaymentDetails
import pl.szczygieldev.ecommercebackend.domain.error.AppError
import pl.szczygieldev.ecommercebackend.domain.error.CartNotFoundError
import pl.szczygieldev.ecommercebackend.domain.event.OrderEvent
import pl.szczygieldev.shared.ddd.core.DomainEventPublisher
import java.net.URL

class CreateOrderCommandHandler(
    val orders: Orders,
    val cartProjections: CartsProjections,
    val paymentService: PaymentService,
    val orderEventPublisher: DomainEventPublisher<OrderEvent>,
    commandResultStorage: CommandResultStorage
) : CommandHandler<CreateOrderCommand>(commandResultStorage) {
    private val paymentReturnUrlBase = "http://localhost:64427/paymentResult/"
    override suspend fun processCommand(command: CreateOrderCommand): Either<AppError, Unit> = either {
        val cartId = command.cartId
        val cart = cartProjections.findById(cartId) ?: raise(CartNotFoundError.forId(cartId))
        val paymentServiceProvider = command.paymentServiceProvider

        val orderId = orders.nextIdentity()
        val paymentRegistration = paymentService.registerPayment(
            cart.amount,
            paymentServiceProvider,
            URL("$paymentReturnUrlBase${orderId.id()}")
        )

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
            command.deliveryProvider,
            cart.items.map { cartItem -> Order.OrderItem(cartItem.productId, cartItem.quantity) }
        )

        val orderVersion = order.version
        val events = order.occurredEvents()
        orders.save(order, orderVersion)
        orderEventPublisher.publishBatch(events)
    }
}