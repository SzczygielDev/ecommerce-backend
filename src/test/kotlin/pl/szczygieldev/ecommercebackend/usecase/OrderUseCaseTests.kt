package pl.szczygieldev.ecommercebackend.usecase

import arrow.core.raise.either
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.*
import pl.szczygieldev.ecommercebackend.application.OrderService
import pl.szczygieldev.ecommercebackend.application.handlers.*
import pl.szczygieldev.ecommercebackend.application.handlers.common.CommandId
import pl.szczygieldev.ecommercebackend.application.model.CartProjection
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.*
import pl.szczygieldev.ecommercebackend.application.port.out.*
import pl.szczygieldev.ecommercebackend.domain.*
import pl.szczygieldev.ecommercebackend.domain.error.AppError
import pl.szczygieldev.ecommercebackend.domain.error.CartNotFoundError
import pl.szczygieldev.ecommercebackend.domain.error.OrderNotFoundError
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
    val cartCreateCommandHandler: CartCreateCommandHandler =
        spyk(CartCreateCommandHandler(cartsMock, cartEventPublisherMock, commandResultStorage))

    val orderService = OrderService(
        orderEventPublisherMock,
        acceptOrderCommandHandler,
        rejectOrderCommandHandler,
        cancelOrderCommandHandler,
        returnOrderCommandHandler,
        createOrderCommandHandler,
        cartCreateCommandHandler
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

        context("AcceptOrderCommandHandler tests") {
            val command = AcceptOrderCommand(CommandId(), orderId)
            test("OrderNotFoundError should be raised when order not found") {
                //Arrange
                every { ordersMock.findById(any()) } returns null

                //Act
                val result = acceptOrderCommandHandler.execute(command)

                //Assert
                result.isLeft().shouldBe(true)
                val error = result.leftOrNull().shouldNotBeNull()
                error.shouldBeInstanceOf<OrderNotFoundError>()
            }

            test("Order accept should be called when order was found") {
                //Arrange
                val order = spyk(order)
                every { ordersMock.findById(orderId) } returns order

                //Act
                acceptOrderCommandHandler.execute(command)

                //Assert
                verify { order.accept() }
            }

            test("Order should be saved when no error occurred") {
                //Arrange
                every { ordersMock.findById(orderId) } returns order

                //Act
                acceptOrderCommandHandler.execute(command)

                //Assert
                verify { ordersMock.save(order, any()) }
            }

            test("Order occurred events should be published when no error occurred") {
                //Arrange
                every { ordersMock.findById(orderId) } returns order

                //Act
                acceptOrderCommandHandler.execute(command)

                //Assert
                verify { orderEventPublisherMock.publishBatch(order.occurredEvents()) }
            }
        }
        context("RejectOrderCommandHandler tests") {
            val command = RejectOrderCommand(CommandId(), orderId)
            test("OrderNotFoundError should be raised when order not found") {
                //Arrange
                every { ordersMock.findById(any()) } returns null

                //Act
                val result = rejectOrderCommandHandler.execute(command)

                //Assert
                result.isLeft().shouldBe(true)
                val error = result.leftOrNull().shouldNotBeNull()
                error.shouldBeInstanceOf<OrderNotFoundError>()
            }

            test("Order reject should be called when order was found") {
                //Arrange
                val order = spyk(order)
                every { ordersMock.findById(orderId) } returns order

                //Act
                rejectOrderCommandHandler.execute(command)

                //Assert
                verify { order.reject() }
            }

            test("Order should be saved when no error occurred") {
                //Arrange
                every { ordersMock.findById(orderId) } returns order

                //Act
                rejectOrderCommandHandler.execute(command)

                //Assert
                verify { ordersMock.save(order, any()) }
            }

            test("Order occurred events should be published when no error occurred") {
                //Arrange
                every { ordersMock.findById(orderId) } returns order

                //Act
                rejectOrderCommandHandler.execute(command)

                //Assert
                verify { orderEventPublisherMock.publishBatch(order.occurredEvents()) }
            }
        }
        context("CancelOrderCommandHandler tests") {
            val command = CancelOrderCommand(CommandId(), orderId)
            test("OrderNotFoundError should be raised when order not found") {
                //Arrange
                every { ordersMock.findById(any()) } returns null

                //Act
                val result = cancelOrderCommandHandler.execute(command)

                //Assert
                result.isLeft().shouldBe(true)
                val error = result.leftOrNull().shouldNotBeNull()
                error.shouldBeInstanceOf<OrderNotFoundError>()
            }

            test("Order cancel should be called when order was found") {
                //Arrange
                val order = spyk(order)
                every { ordersMock.findById(orderId) } returns order

                //Act
                cancelOrderCommandHandler.execute(command)

                //Assert
                verify { order.cancel() }
            }

            test("Order should be saved when no error occurred") {
                //Arrange
                every { ordersMock.findById(orderId) } returns order

                //Act
                cancelOrderCommandHandler.execute(command)

                //Assert
                verify { ordersMock.save(order, any()) }
            }

            test("Order occurred events should be published when no error occurred") {
                //Arrange
                every { ordersMock.findById(orderId) } returns order

                //Act
                cancelOrderCommandHandler.execute(command)

                //Assert
                verify { orderEventPublisherMock.publishBatch(order.occurredEvents()) }
            }


        }
        context("ReturnOrderCommandHandler tests") {
            val command = ReturnOrderCommand(CommandId(), orderId)
            order.accept()
            order.beginPacking()
            order.completePacking(parcelId, dimensions)
            order.changeDeliveryStatus(DeliveryStatus.DELIVERED)
            test("OrderNotFoundError should be raised when order not found") {
                //Arrange
                every { ordersMock.findById(any()) } returns null

                //Act
                val result = returnOrderCommandHandler.execute(command)

                //Assert
                result.isLeft().shouldBe(true)
                val error = result.leftOrNull().shouldNotBeNull()
                error.shouldBeInstanceOf<OrderNotFoundError>()
            }

            test("Order return should be called when order was found") {
                //Arrange
                val order = spyk(order)
                every { ordersMock.findById(orderId) } returns order

                //Act
                returnOrderCommandHandler.execute(command)

                //Assert
                verify { order.returnOrder() }
            }

            test("Order should be saved when no error occurred") {
                //Arrange
                every { ordersMock.findById(orderId) } returns order

                //Act
                returnOrderCommandHandler.execute(command)

                //Assert
                verify { ordersMock.save(order, any()) }
            }

            test("Order occurred events should be published when no error occurred") {
                //Arrange
                every { ordersMock.findById(orderId) } returns order

                //Act
                returnOrderCommandHandler.execute(command)

                //Assert
                verify { orderEventPublisherMock.publishBatch(order.occurredEvents()) }
            }

        }
        context("CreateOrderCommandHandler tests") {
            val command = CreateOrderCommand(cartId, psp, deliveryProvider)
            test("CartNotFoundError should be raised when cart was not found") {
                //Arrange
                every { cartsProjectionsMock.findById(cartId) } returns null

                //Act
                val result = createOrderCommandHandler.execute(command)

                //Assert
                result.isLeft().shouldBe(true)
                val error = result.leftOrNull().shouldNotBeNull()
                error.shouldBeInstanceOf<CartNotFoundError>()
            }

            test("PaymentService should be called with provided amount, payment service provided and return url") {
                //Arrange

                //Act
                createOrderCommandHandler.execute(command)

                //Assert
                verify {
                    paymentServiceMock.registerPayment(
                        amount,
                        psp,
                        URL("${CreateOrderCommandHandler.paymentReturnUrlBase}${orderId.id()}")
                    )
                }
            }

            test("Order should be created with provided parameters and payment data from PaymentService") {
                //Arrange

                //Act
                createOrderCommandHandler.execute(command)

                //Assert
                val order = orderSlot.captured
                val payment = order.payment
                order.orderId.sameValueAs(orderId).shouldBe(true)
                order.cartId.sameValueAs(cartId).shouldBe(true)
                order.delivery.deliveryProvider.shouldBe(deliveryProvider)
                order.items.size.shouldBe(cartProjection.items.size)
                order.items.filter { orderItem -> orderItem.productId.sameValueAs(cartEntry.productId) }
                    .shouldNotBeEmpty()
                order.items.first { orderItem -> orderItem.productId.sameValueAs(cartEntry.productId) }.quantity.shouldBe(
                    cartItemQuantity
                )

                payment.id.sameValueAs(paymentId).shouldBe(true)
                payment.amount.shouldBe(amount)
                payment.url.shouldBe(paymentUrl)
                payment.paymentServiceProvider.shouldBe(psp)
            }
        }

        test("Creating order should call CreateOrderCommandHandler") {
            //Arrange
            val command = CreateOrderCommand(cartId, psp, deliveryProvider)

            //Act
            orderService.createOrder(command)

            //Assert
            coVerify { createOrderCommandHandler.execute(command) }
        }

        test("Creating order should call CartCreateCommandHandler") {
            //Arrange
            val command = CreateOrderCommand(cartId, psp, deliveryProvider)

            //Act
            orderService.createOrder(command)

            //Assert
            coVerify { cartCreateCommandHandler.execute(any()) }
        }

        test("Accepting order should call AcceptOrderCommandHandler") {
            //Arrange
            val command = AcceptOrderCommand(CommandId(),orderId)

            //Act
            orderService.acceptOrder(command)

            //Assert
            coVerify { acceptOrderCommandHandler.executeInBackground(any()) }
        }

        test("Rejecting order should call RejectOrderCommandHandler") {
            //Arrange
            val command = RejectOrderCommand(CommandId(),orderId)

            //Act
            orderService.rejectOrder(command)

            //Assert
            coVerify { rejectOrderCommandHandler.executeInBackground(any()) }
        }

        test("Canceling order should call CancelOrderCommandHandler") {
            //Arrange
            val command = CancelOrderCommand(CommandId(),orderId)

            //Act
            orderService.cancelOrder(command)

            //Assert
            coVerify { cancelOrderCommandHandler.executeInBackground(any()) }
        }

        test("Returning order should call ReturnOrderCommandHandler") {
            //Arrange
            val command = ReturnOrderCommand(CommandId(),orderId)

            //Act
            orderService.returnOrder(command)

            //Assert
            coVerify { returnOrderCommandHandler.executeInBackground(any()) }
        }
    }
}