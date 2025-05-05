package pl.szczygieldev.order.application

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.ecommercelibrary.ddd.core.DomainEventPublisher
import pl.szczygieldev.order.application.port.`in`.command.CreateOrderCommand
import pl.szczygieldev.order.application.port.out.Carts
import pl.szczygieldev.order.application.port.out.Orders
import pl.szczygieldev.order.application.port.out.PaymentService
import pl.szczygieldev.order.domain.Order
import pl.szczygieldev.order.domain.PaymentDetails
import pl.szczygieldev.order.domain.event.OrderEvent
import java.net.URL

internal class CreateOrderCommandHandler(
    val orderEventPublisher: DomainEventPublisher<OrderEvent>,
    val orders: Orders,
    val carts: Carts,
    val paymentService: PaymentService,
) : CommandWithResultHandler<CreateOrderCommand, Either<CreateOrderCommand.Error, Unit>> {
    companion object {
        val paymentReturnUrlBase = "http://localhost:64427/paymentResult/"
    }

    override suspend fun handle(command: CreateOrderCommand): Either<CreateOrderCommand.Error, Unit> = either {
        val cartId = command.cartId
        val cart = carts.findById(cartId) ?: raise(CreateOrderCommand.CartNotFoundError())
        val paymentServiceProvider = command.paymentServiceProvider

        val orderId = orders.nextIdentity()
        val paymentRegistration = paymentService.registerPayment(
            cart.total,
            paymentServiceProvider,
            URL("$paymentReturnUrlBase${orderId.id()}")
        ) ?: raise(CreateOrderCommand.CannotRegisterPaymentError.forPsp(paymentServiceProvider))

        val order = Order.create(
            orderId,
            cart.id,
            PaymentDetails(
                paymentRegistration.id,
                cart.total,
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