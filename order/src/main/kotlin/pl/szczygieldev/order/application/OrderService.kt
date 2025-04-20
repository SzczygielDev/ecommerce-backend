package pl.szczygieldev.order.application

import arrow.core.Either
import arrow.core.raise.either
import pl.szczygieldev.ecommercelibrary.architecture.UseCase
import pl.szczygieldev.ecommercelibrary.ddd.core.DomainEventPublisher
import pl.szczygieldev.order.application.port.`in`.OrderUseCase
import pl.szczygieldev.order.application.port.`in`.command.*
import pl.szczygieldev.order.application.port.out.Carts
import pl.szczygieldev.order.application.port.out.Orders
import pl.szczygieldev.order.application.port.out.PaymentService
import pl.szczygieldev.order.application.port.out.Products
import pl.szczygieldev.order.domain.Order
import pl.szczygieldev.order.domain.PaymentDetails
import pl.szczygieldev.order.domain.error.*
import pl.szczygieldev.order.domain.event.OrderEvent
import java.net.URL

@UseCase
internal class OrderService(
    val orderEventPublisher: DomainEventPublisher<OrderEvent>,
    val orders: Orders,
    val carts: Carts,
    val paymentService: PaymentService,
    val products: Products,
) : OrderUseCase {
    companion object {
        val paymentReturnUrlBase = "http://localhost:64427/paymentResult/"
    }

    override suspend fun createOrder(command: CreateOrderCommand): Either<CreateOrderCommand.Error, Unit> = either {
        val cartId = command.cartId
        val cart = carts.findById(cartId) ?: raise(CreateOrderCommand.CartNotFoundError())
        val paymentServiceProvider = command.paymentServiceProvider

        val orderId = orders.nextIdentity()
        val paymentRegistration = paymentService.registerPayment(
            cart.total,
            paymentServiceProvider,
            URL("${paymentReturnUrlBase}${orderId.id()}")
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

    override suspend fun acceptOrder(command: AcceptOrderCommand): Either<AcceptOrderCommand.Error, Unit> = either {
        val orderId = command.orderId
        val order = orders.findById(orderId) ?: raise(AcceptOrderCommand.OrderNotFoundError.forId(orderId))
        val orderVersion = order.version

        val result = order.accept()
        result.onLeft { error ->
            when (error) {
                is AcceptError.AlreadyAcceptedOrderError -> AcceptOrderCommand.AlreadyAcceptedOrderError.fromDomainError(
                    error
                )
            }
        }

        val events = order.occurredEvents()
        orders.save(order, orderVersion)
        orderEventPublisher.publishBatch(events)
    }

    override suspend fun rejectOrder(command: RejectOrderCommand): Either<RejectOrderCommand.Error, Unit> = either {
        val orderId = command.orderId
        val order = orders.findById(orderId) ?: raise(RejectOrderCommand.OrderNotFoundError.forId(orderId))
        val orderVersion = order.version

        val result = order.reject()
        result.onLeft { error ->
            when (error) {
                is RejectError.AlreadyAcceptedOrderError -> RejectOrderCommand.AlreadyAcceptedOrderError.fromDomainError(
                    error
                )
            }
        }

        val events = order.occurredEvents()
        orders.save(order, orderVersion)
        orderEventPublisher.publishBatch(events)

    }

    override suspend fun cancelOrder(command: CancelOrderCommand): Either<CancelOrderCommand.Error, Unit> = either {
        val orderId = command.orderId
        val order = orders.findById(orderId) ?: raise(CancelOrderCommand.OrderNotFoundError.forId(orderId))
        val orderVersion = order.version

        val result = order.cancel()
        result.onLeft { error ->
            when (error) {
                is CancelError.CannotCancelSentOrderError -> CancelOrderCommand.CannotCancelSentOrderError.fromDomainError(
                    error
                )
            }
        }

        val events = order.occurredEvents()
        orders.save(order, orderVersion)
        orderEventPublisher.publishBatch(events)
    }

    override suspend fun returnOrder(command: ReturnOrderCommand): Either<ReturnOrderCommand.Error, Unit> = either {
        val orderId = command.orderId
        val order = orders.findById(orderId) ?: raise(ReturnOrderCommand.OrderNotFoundError.forId(orderId))
        val orderVersion = order.version

        val result = order.returnOrder()
        result.onLeft { error ->
            when (error) {
                is ReturnError.CannotReturnNotReceivedOrderError -> ReturnOrderCommand.CannotReturnNotReceivedOrderError.fromDomainError(
                    error
                )
            }
        }

        val events = order.occurredEvents()
        orders.save(order, orderVersion)
        orderEventPublisher.publishBatch(events)
    }
}