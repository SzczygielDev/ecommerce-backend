package pl.szczygieldev.order.application

import arrow.core.Either
import arrow.core.mapOrAccumulate
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
import pl.szczygieldev.order.domain.PriceCalculator
import pl.szczygieldev.order.domain.error.AppError
import pl.szczygieldev.order.domain.error.CannotRegisterPaymentError
import pl.szczygieldev.order.domain.error.CartNotFoundError
import pl.szczygieldev.order.domain.error.MissingProductForCalculateError
import pl.szczygieldev.order.domain.error.OrderNotFoundError
import pl.szczygieldev.order.domain.error.UnableToCalculateCartTotalError
import pl.szczygieldev.order.domain.event.OrderEvent
import java.net.URL

@UseCase
internal class OrderService(
    val orderEventPublisher: DomainEventPublisher<OrderEvent>,
    val orders: Orders,
    val carts: Carts,
    val paymentService: PaymentService,
    val products: Products,
    val priceCalculator: PriceCalculator,
) : OrderUseCase {
    companion object {
        val paymentReturnUrlBase = "http://localhost:64427/paymentResult/"
    }

    override suspend fun createOrder(command: CreateOrderCommand): Either<AppError, Unit> = either {
        val cartId = command.cartId
        val cart = carts.findById(cartId) ?: raise(CartNotFoundError.forId(cartId))
        val paymentServiceProvider = command.paymentServiceProvider

        val total = cart.items.mapOrAccumulate { cartEntry ->
            val product = products.findById(cartEntry.productId)
                ?: raise(MissingProductForCalculateError.forProduct(cartEntry.productId))

            return@mapOrAccumulate product
        }.fold({ errors ->
            val ids = errors.map { error -> error.productId.id() }.toList().toString()
            raise(UnableToCalculateCartTotalError("Failed to fetch products with ids='$ids'"))
        }, { products ->
            priceCalculator.calculate(cart, products) ?: raise(UnableToCalculateCartTotalError("Failed to calculate cart"))
        })

        val orderId = orders.nextIdentity()
        val paymentRegistration = paymentService.registerPayment(
            total,
            paymentServiceProvider,
            URL("${paymentReturnUrlBase}${orderId.id()}")
        ) ?: raise(CannotRegisterPaymentError.forPsp(paymentServiceProvider))

        val order = Order.create(
            orderId,
            cart.cartId,
            PaymentDetails(
                paymentRegistration.id,
                total,
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

    override suspend fun acceptOrder(command: AcceptOrderCommand): Either<AppError, Unit> = either {
        val orderId = command.orderId
        val order = orders.findById(orderId) ?: raise(OrderNotFoundError.forId(orderId))
        val orderVersion = order.version
        order.accept().bind()
        val events = order.occurredEvents()
        orders.save(order, orderVersion)
        orderEventPublisher.publishBatch(events)
    }

    override suspend fun rejectOrder(command: RejectOrderCommand): Either<AppError, Unit> = either {
        val orderId = command.orderId
        val order = orders.findById(orderId) ?: raise(OrderNotFoundError.forId(orderId))
        val orderVersion = order.version

        order.reject().bind()

        val events = order.occurredEvents()
        orders.save(order, orderVersion)
        orderEventPublisher.publishBatch(events)

    }

    override suspend fun cancelOrder(command: CancelOrderCommand): Either<AppError, Unit> = either {
        val orderId = command.orderId
        val order = orders.findById(orderId) ?: raise(OrderNotFoundError.forId(orderId))
        val orderVersion = order.version
        order.cancel().bind()
        val events = order.occurredEvents()
        orders.save(order, orderVersion)
        orderEventPublisher.publishBatch(events)
    }

    override suspend fun returnOrder(command: ReturnOrderCommand): Either<AppError, Unit> = either {
        val orderId = command.orderId
        val order = orders.findById(orderId) ?: raise(OrderNotFoundError.forId(orderId))
        val orderVersion = order.version

        order.returnOrder().bind()

        val events = order.occurredEvents()
        orders.save(order, orderVersion)
        orderEventPublisher.publishBatch(events)
    }
}