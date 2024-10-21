package pl.szczygieldev.ecommercebackend.application

import arrow.core.raise.either
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.mockk.*
import pl.szczygieldev.ecommercebackend.application.handlers.*
import pl.szczygieldev.ecommercebackend.application.handlers.common.CommandId
import pl.szczygieldev.ecommercebackend.application.model.CartProjection
import pl.szczygieldev.ecommercebackend.application.port.`in`.CartUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.*
import pl.szczygieldev.ecommercebackend.application.port.out.*
import pl.szczygieldev.ecommercebackend.domain.*
import pl.szczygieldev.ecommercebackend.domain.error.AppError
import pl.szczygieldev.ecommercebackend.domain.event.CartEvent
import pl.szczygieldev.ecommercebackend.domain.event.OrderEvent
import pl.szczygieldev.shared.ddd.core.DomainEventPublisher
import java.math.BigDecimal
import java.net.URL
import java.util.UUID

class OrderUseCaseTests : FunSpec() {
    val ordersMock = mockk<Orders>()
    val cartsProjectionsMock = mockk<CartsProjections>()
    val cartsMock = mockk<Carts>()
    val orderEventPublisherMock = mockk<DomainEventPublisher<OrderEvent>>()
    val cartEventPublisherMock = mockk<DomainEventPublisher<CartEvent>>()
    val commandResultStorage: CommandResultStorage = mockk<CommandResultStorage>()
    val paymentServiceMock = mockk<PaymentService>()

    val acceptOrderCommandHandler: AcceptOrderCommandHandler =
        spyk(AcceptOrderCommandHandler(ordersMock, orderEventPublisherMock, commandResultStorage))
    val rejectOrderCommandHandler: RejectOrderCommandHandler =
        spyk(RejectOrderCommandHandler(ordersMock, orderEventPublisherMock, commandResultStorage))
    val cancelOrderCommandHandler: CancelOrderCommandHandler =
        spyk(CancelOrderCommandHandler(ordersMock, orderEventPublisherMock, commandResultStorage))
    val returnOrderCommandHandler: ReturnOrderCommandHandler =
        spyk(ReturnOrderCommandHandler(ordersMock, orderEventPublisherMock, commandResultStorage))
    val createOrderCommandHandler: CreateOrderCommandHandler = spyk(
        CreateOrderCommandHandler(
            ordersMock,
            cartsProjectionsMock,
            paymentServiceMock,
            orderEventPublisherMock,
            commandResultStorage
        )
    )
    val cartUseCase = mockk<CartUseCase>()
    val orderService = OrderService(
        orderEventPublisherMock,
        acceptOrderCommandHandler,
        rejectOrderCommandHandler,
        cancelOrderCommandHandler,
        returnOrderCommandHandler,
        createOrderCommandHandler,
        cartUseCase
    )

    init {
        isolationMode = IsolationMode.InstancePerLeaf
        every { ordersMock.save(any(), any()) } just runs
        every { orderEventPublisherMock.publish(any()) } just runs
        every { orderEventPublisherMock.publishBatch(any()) } just runs
        coEvery { commandResultStorage.commandBegin(any()) } returns either { }
        coEvery { commandResultStorage.commandSuccess(any()) } returns either { }
        coEvery { commandResultStorage.commandFailed(any(), any<AppError>()) } returns either { }
        coEvery { commandResultStorage.commandFailed(any(), any<List<AppError>>()) } returns either { }
        coEvery { cartUseCase.createCart(any()) } returns either { }
        val orderId = OrderId(UUID.randomUUID().toString())
        val amount = BigDecimal.TEN
        val psp = PaymentServiceProvider.MOCK_PSP
        val deliveryProvider = DeliveryProvider.MOCK_DELIVERY_PROVIDER
        val cartId = CartId(UUID.randomUUID().toString())

        val paymentId = PaymentId(
            UUID.randomUUID().toString()
        )
        val paymentUrl = URL("http://localhost:3000/")
        val paymentRegistration = PaymentRegistration(
            paymentId, paymentUrl
        )

        val order = Order.create(
            orderId,
            cartId,
            PaymentDetails(
                paymentId, amount, paymentUrl,
                psp
            ),
            deliveryProvider,
            emptyList()
        )
        val dimensions = ParcelDimensions(10.0, 25.0, 30.0, 5.0)
        val parcelId = ParcelId(UUID.randomUUID().toString())

        val cartItemQuantity = 1
        val cartEntry = CartProjection.Entry(ProductId(UUID.randomUUID().toString()), cartItemQuantity)
        val cartProjection = CartProjection(cartId, CartStatus.SUBMITTED, amount, listOf(cartEntry))

        every { ordersMock.nextIdentity() } returns orderId
        val orderSlot = slot<Order>()
        every { ordersMock.save(capture(orderSlot), any()) } just runs

        every { paymentServiceMock.registerPayment(any(), any(), any()) } returns paymentRegistration

        every { cartsMock.nextIdentity() } returns cartId
        every { cartsMock.save(any(), any()) } just runs
        every { cartEventPublisherMock.publishBatch(any()) } just runs
        every { cartsProjectionsMock.findById(cartId) } returns cartProjection

        test("Creating order should call CreateOrderCommandHandler") {
            //Arrange
            val command = CreateOrderCommand(cartId, psp, deliveryProvider)

            //Act
            orderService.createOrder(command)

            //Assert
            coVerify { createOrderCommandHandler.execute(command) }
        }

        test("Creating order should create cart") {
            //Arrange
            val command = CreateOrderCommand(cartId, psp, deliveryProvider)

            //Act
            orderService.createOrder(command)

            //Assert
            coVerify { cartUseCase.createCart(any()) }
        }

        test("Accepting order should call AcceptOrderCommandHandler") {
            //Arrange
            val command = AcceptOrderCommand(CommandId(), orderId)

            //Act
            orderService.acceptOrder(command)

            //Assert
            coVerify { acceptOrderCommandHandler.executeInBackground(any()) }
        }

        test("Rejecting order should call RejectOrderCommandHandler") {
            //Arrange
            val command = RejectOrderCommand(CommandId(), orderId)

            //Act
            orderService.rejectOrder(command)

            //Assert
            coVerify { rejectOrderCommandHandler.executeInBackground(any()) }
        }

        test("Canceling order should call CancelOrderCommandHandler") {
            //Arrange
            val command = CancelOrderCommand(CommandId(), orderId)

            //Act
            orderService.cancelOrder(command)

            //Assert
            coVerify { cancelOrderCommandHandler.executeInBackground(any()) }
        }

        test("Returning order should call ReturnOrderCommandHandler") {
            //Arrange
            val command = ReturnOrderCommand(CommandId(), orderId)

            //Act
            orderService.returnOrder(command)

            //Assert
            coVerify { returnOrderCommandHandler.executeInBackground(any()) }
        }
    }
}