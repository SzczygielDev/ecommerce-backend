package pl.szczygieldev.order.application

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.*
import pl.szczygieldev.ecommercelibrary.command.CommandId
import pl.szczygieldev.ecommercelibrary.ddd.core.DomainEventPublisher
import pl.szczygieldev.order.application.port.`in`.command.*
import pl.szczygieldev.order.application.port.out.*
import pl.szczygieldev.order.domain.*
import pl.szczygieldev.order.domain.event.OrderEvent
import java.math.BigDecimal
import java.net.URL
import java.util.UUID

internal class OrderUseCaseTests : FunSpec() {
    val orderEventPublisherMock = mockk<DomainEventPublisher<OrderEvent>>()
    val ordersMock = mockk<Orders>()
    val cartsMock = mockk<Carts>()
    val productsMock = mockk<Products>()
    val paymentServiceMock = mockk<PaymentService>()

    val createOrderCommandHandler =
        CreateOrderCommandHandler(orderEventPublisherMock, ordersMock, cartsMock, paymentServiceMock)
    val acceptOrderCommandHandler = AcceptOrderCommandHandler(orderEventPublisherMock, ordersMock)
    val rejectOrderCommandHandler = RejectOrderCommandHandler(orderEventPublisherMock, ordersMock)
    val cancelOrderCommandHandler = CancelOrderCommandHandler(orderEventPublisherMock, ordersMock)
    val returnOrderCommandHandler = ReturnOrderCommandHandler(orderEventPublisherMock, ordersMock)

    init {
        isolationMode = IsolationMode.InstancePerLeaf
        coEvery { ordersMock.save(any(), any()) } just runs
        every { orderEventPublisherMock.publish(any()) } just runs
        every { orderEventPublisherMock.publishBatch(any()) } just runs

        val orderId = OrderId(UUID.randomUUID())
        val amount = BigDecimal.TEN
        val psp = PaymentServiceProvider.MOCK_PSP
        val deliveryProvider = DeliveryProvider.MOCK_DELIVERY_PROVIDER
        val cartId = CartId(UUID.randomUUID())

        val paymentId = PaymentId(UUID.randomUUID())
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
        val parcelId = ParcelId(UUID.randomUUID())
        val cartItemQuantity = 1
        val productId = ProductId(UUID.randomUUID())
        val productPrice = BigDecimal.TEN
        val product = Product(productId, "Product title", productPrice, ImageId(UUID.randomUUID()))
        val cart = Cart(
            cartId,
            listOf(Cart.Item(productId, cartItemQuantity)),
            productPrice * BigDecimal.valueOf(cartItemQuantity.toLong())
        )


        every { ordersMock.nextIdentity() } returns orderId
        val orderSlot = slot<Order>()
        coEvery { ordersMock.save(capture(orderSlot), any()) } just runs

        every { paymentServiceMock.registerPayment(any(), any(), any()) } returns paymentRegistration
        every { cartsMock.findById(cartId) } returns cart
        every { productsMock.findById(productId) } returns product

        context("Order create") {
            val command = CreateOrderCommand(cartId, psp, deliveryProvider)
            test("CartNotFoundError should be raised when cart was not found") {
                //Arrange
                every { cartsMock.findById(cartId) } returns null

                //Act
                val result = createOrderCommandHandler.handle(command)

                //Assert
                result.isLeft().shouldBe(true)
                val error = result.leftOrNull().shouldNotBeNull()
                error.shouldBeInstanceOf<CreateOrderCommand.CartNotFoundError>()
            }

            test("PaymentService should be called with provided amount, payment service provided and return url") {
                //Arrange

                //Act
                val result = createOrderCommandHandler.handle(command)

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
                val command = CreateOrderCommand(cartId, psp, deliveryProvider)

                //Act
                val result = createOrderCommandHandler.handle(command)

                //Assert
                val order = orderSlot.captured
                val payment = order.payment
                order.orderId.sameValueAs(orderId).shouldBe(true)
                order.cartId.sameValueAs(cartId).shouldBe(true)
                order.delivery.deliveryProvider.shouldBe(deliveryProvider)
                // order.items.size.shouldBe(cart.items.size)
                order.items.filter { orderItem -> orderItem.productId.sameValueAs(productId) }
                    .shouldNotBeEmpty()
                order.items.first { orderItem -> orderItem.productId.sameValueAs(productId) }.quantity.shouldBe(
                    cartItemQuantity
                )

                payment.id.sameValueAs(paymentId).shouldBe(true)
                payment.amount.shouldBe(amount)
                payment.url.shouldBe(paymentUrl)
                payment.paymentServiceProvider.shouldBe(psp)
            }
        }

        context("Order accept") {
            val command = AcceptOrderCommand(CommandId(), orderId)
            test("OrderNotFoundError should be raised when order not found") {
                //Arrange
                every { ordersMock.findById(any()) } returns null

                //Act
                val result = acceptOrderCommandHandler.handle(command)

                //Assert
                result.isLeft().shouldBe(true)
                val error = result.leftOrNull().shouldNotBeNull()
                error.shouldBeInstanceOf<AcceptOrderCommand.OrderNotFoundError>()
            }

            test("Order accept should be called when order was found") {
                //Arrange
                val order = spyk(order)
                every { ordersMock.findById(orderId) } returns order

                //Act
                val result = acceptOrderCommandHandler.handle(command)

                //Assert
                verify { order.accept() }
            }

            test("Order should be saved when no error occurred") {
                //Arrange
                every { ordersMock.findById(orderId) } returns order

                //Act
                val result = acceptOrderCommandHandler.handle(command)

                //Assert
                coVerify { ordersMock.save(order, any()) }
            }

            test("Order occurred events should be published when no error occurred") {
                //Arrange
                every { ordersMock.findById(orderId) } returns order

                //Act
                val result = acceptOrderCommandHandler.handle(command)

                //Assert
                verify { orderEventPublisherMock.publishBatch(order.occurredEvents()) }
            }
        }

        context("Order reject") {
            val command = RejectOrderCommand(CommandId(), orderId)
            test("OrderNotFoundError should be raised when order not found") {
                //Arrange
                every { ordersMock.findById(any()) } returns null

                //Act
                val result = rejectOrderCommandHandler.handle(command)

                //Assert
                result.isLeft().shouldBe(true)
                val error = result.leftOrNull().shouldNotBeNull()
                error.shouldBeInstanceOf<RejectOrderCommand.OrderNotFoundError>()
            }

            test("Order reject should be called when order was found") {
                //Arrange
                val order = spyk(order)
                every { ordersMock.findById(orderId) } returns order

                //Act
                rejectOrderCommandHandler.handle(command)

                //Assert
                verify { order.reject() }
            }

            test("Order should be saved when no error occurred") {
                //Arrange
                every { ordersMock.findById(orderId) } returns order

                //Act
                rejectOrderCommandHandler.handle(command)

                //Assert
                coVerify { ordersMock.save(order, any()) }
            }

            test("Order occurred events should be published when no error occurred") {
                //Arrange
                every { ordersMock.findById(orderId) } returns order

                //Act
                rejectOrderCommandHandler.handle(command)

                //Assert
                verify { orderEventPublisherMock.publishBatch(order.occurredEvents()) }
            }
        }

        context("Order cancel") {
            val command = CancelOrderCommand(CommandId(), orderId)
            test("OrderNotFoundError should be raised when order not found") {
                //Arrange
                every { ordersMock.findById(any()) } returns null

                //Act
                val result = cancelOrderCommandHandler.handle(command)

                //Assert
                result.isLeft().shouldBe(true)
                val error = result.leftOrNull().shouldNotBeNull()
                error.shouldBeInstanceOf<CancelOrderCommand.OrderNotFoundError>()
            }

            test("Order cancel should be called when order was found") {
                //Arrange
                val order = spyk(order)
                every { ordersMock.findById(orderId) } returns order

                //Act
                cancelOrderCommandHandler.handle(command)

                //Assert
                verify { order.cancel() }
            }

            test("Order should be saved when no error occurred") {
                //Arrange
                every { ordersMock.findById(orderId) } returns order

                //Act
                cancelOrderCommandHandler.handle(command)

                //Assert
                coVerify { ordersMock.save(order, any()) }
            }

            test("Order occurred events should be published when no error occurred") {
                //Arrange
                every { ordersMock.findById(orderId) } returns order

                //Act
                cancelOrderCommandHandler.handle(command)

                //Assert
                verify { orderEventPublisherMock.publishBatch(order.occurredEvents()) }
            }
        }

        context("Order return") {
            val command = ReturnOrderCommand(CommandId(), orderId)
            order.accept()
            order.beginPacking()
            order.completePacking(parcelId, dimensions)
            order.changeDeliveryStatus(DeliveryStatus.DELIVERED)
            test("OrderNotFoundError should be raised when order not found") {
                //Arrange
                every { ordersMock.findById(any()) } returns null

                //Act
                val result = returnOrderCommandHandler.handle(command)

                //Assert
                result.isLeft().shouldBe(true)
                val error = result.leftOrNull().shouldNotBeNull()
                error.shouldBeInstanceOf<ReturnOrderCommand.OrderNotFoundError>()
            }

            test("Order return should be called when order was found") {
                //Arrange
                val order = spyk(order)
                every { ordersMock.findById(orderId) } returns order

                //Act
                returnOrderCommandHandler.handle(command)

                //Assert
                verify { order.returnOrder() }
            }

            test("Order should be saved when no error occurred") {
                //Arrange
                every { ordersMock.findById(orderId) } returns order

                //Act
                returnOrderCommandHandler.handle(command)

                //Assert
                coVerify { ordersMock.save(order, any()) }
            }

            test("Order occurred events should be published when no error occurred") {
                //Arrange
                every { ordersMock.findById(orderId) } returns order

                //Act
                returnOrderCommandHandler.handle(command)

                //Assert
                verify { orderEventPublisherMock.publishBatch(order.occurredEvents()) }
            }
        }
    }
}