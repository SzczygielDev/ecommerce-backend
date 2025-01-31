package pl.szczygieldev.order.application

import arrow.core.raise.either
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.*
import pl.szczygieldev.ecommercelibrary.command.CommandId
import pl.szczygieldev.ecommercelibrary.ddd.core.DomainEventPublisher
import pl.szczygieldev.order.application.port.`in`.CartUseCase
import pl.szczygieldev.order.application.port.`in`.command.*
import pl.szczygieldev.order.application.port.out.*
import pl.szczygieldev.order.domain.*
import pl.szczygieldev.order.domain.error.CartNotFoundError
import pl.szczygieldev.order.domain.error.OrderNotFoundError
import pl.szczygieldev.order.domain.event.OrderEvent
import java.math.BigDecimal
import java.net.URL
import java.util.UUID

internal class OrderUseCaseTests : FunSpec() {
    val orderEventPublisherMock = mockk<DomainEventPublisher<OrderEvent>>()
    val ordersMock = mockk<Orders>()
    val cartUseCase = mockk<CartUseCase>()
    val cartsMock = mockk<Carts>()
    val productsMock = mockk<Products>()
    val paymentServiceMock = mockk<PaymentService>()
    val priceCalculator = PriceCalculator()
    val orderService = OrderService(
        orderEventPublisherMock,
        ordersMock,
        cartsMock,
        paymentServiceMock,
        productsMock,
        priceCalculator
    )


    init {
        isolationMode = IsolationMode.InstancePerLeaf
        every { ordersMock.save(any(), any()) } just runs
        every { orderEventPublisherMock.publish(any()) } just runs
        every { orderEventPublisherMock.publishBatch(any()) } just runs
        coEvery { cartUseCase.createCart(any()) } returns either { }
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
        val product = Product(productId,"Product title",BigDecimal.TEN, ImageId(UUID.randomUUID()))
        val cart = Cart.create(cartId)
        cart.addItem(productId,cartItemQuantity)

        every { ordersMock.nextIdentity() } returns orderId
        val orderSlot = slot<Order>()
        every { ordersMock.save(capture(orderSlot), any()) } just runs

        every { paymentServiceMock.registerPayment(any(), any(), any()) } returns paymentRegistration
        every { cartsMock.findById(cartId) } returns cart
        every { productsMock.findById(productId) } returns product

        context("Order create") {
            val command = CreateOrderCommand(cartId, psp, deliveryProvider)
            test("CartNotFoundError should be raised when cart was not found") {
                //Arrange
                every { cartsMock.findById(cartId) } returns null

                //Act
                val result = orderService.createOrder(command)

                //Assert
                result.isLeft().shouldBe(true)
                val error = result.leftOrNull().shouldNotBeNull()
                error.shouldBeInstanceOf<CartNotFoundError>()
            }

            test("PaymentService should be called with provided amount, payment service provided and return url") {
                //Arrange

                //Act
                val result = orderService.createOrder(command)

                //Assert
                verify {
                    paymentServiceMock.registerPayment(
                        amount,
                        psp,
                        URL("${OrderService.paymentReturnUrlBase}${orderId.id()}")
                    )
                }
            }

            test("Order should be created with provided parameters and payment data from PaymentService") {
                //Arrange
                val command = CreateOrderCommand(cartId, psp, deliveryProvider)

                //Act
                val result = orderService.createOrder(command)

                //Assert
                val order = orderSlot.captured
                val payment = order.payment
                order.orderId.sameValueAs(orderId).shouldBe(true)
                order.cartId.sameValueAs(cartId).shouldBe(true)
                order.delivery.deliveryProvider.shouldBe(deliveryProvider)
                order.items.size.shouldBe(cart.items.size)
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
                val result = orderService.acceptOrder(command)

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
                val result = orderService.acceptOrder(command)

                //Assert
                verify { order.accept() }
            }

            test("Order should be saved when no error occurred") {
                //Arrange
                every { ordersMock.findById(orderId) } returns order

                //Act
                val result = orderService.acceptOrder(command)

                //Assert
                verify { ordersMock.save(order, any()) }
            }

            test("Order occurred events should be published when no error occurred") {
                //Arrange
                every { ordersMock.findById(orderId) } returns order

                //Act
                val result = orderService.acceptOrder(command)

                //Assert
                verify { orderEventPublisherMock.publishBatch(order.occurredEvents()) }
            }
        }

        context("Order reject"){
            val command = RejectOrderCommand(CommandId(), orderId)
            test("OrderNotFoundError should be raised when order not found") {
                //Arrange
                every { ordersMock.findById(any()) } returns null

                //Act
                val result = orderService.rejectOrder(command)

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
                orderService.rejectOrder(command)

                //Assert
                verify { order.reject() }
            }

            test("Order should be saved when no error occurred") {
                //Arrange
                every { ordersMock.findById(orderId) } returns order

                //Act
                orderService.rejectOrder(command)

                //Assert
                verify { ordersMock.save(order, any()) }
            }

            test("Order occurred events should be published when no error occurred") {
                //Arrange
                every { ordersMock.findById(orderId) } returns order

                //Act
                orderService.rejectOrder(command)

                //Assert
                verify { orderEventPublisherMock.publishBatch(order.occurredEvents()) }
            }
        }

        context("Order cancel"){
            val command = CancelOrderCommand(CommandId(), orderId)
            test("OrderNotFoundError should be raised when order not found") {
                //Arrange
                every { ordersMock.findById(any()) } returns null

                //Act
                val result = orderService.cancelOrder(command)

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
                orderService.cancelOrder(command)

                //Assert
                verify { order.cancel() }
            }

            test("Order should be saved when no error occurred") {
                //Arrange
                every { ordersMock.findById(orderId) } returns order

                //Act
                orderService.cancelOrder(command)

                //Assert
                verify { ordersMock.save(order, any()) }
            }

            test("Order occurred events should be published when no error occurred") {
                //Arrange
                every { ordersMock.findById(orderId) } returns order

                //Act
                orderService.cancelOrder(command)

                //Assert
                verify { orderEventPublisherMock.publishBatch(order.occurredEvents()) }
            }
        }

        context("Order return"){
            val command = ReturnOrderCommand(CommandId(), orderId)
            order.accept()
            order.beginPacking()
            order.completePacking(parcelId, dimensions)
            order.changeDeliveryStatus(DeliveryStatus.DELIVERED)
            test("OrderNotFoundError should be raised when order not found") {
                //Arrange
                every { ordersMock.findById(any()) } returns null

                //Act
                val result = orderService.returnOrder(command)

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
                orderService.returnOrder(command)

                //Assert
                verify { order.returnOrder() }
            }

            test("Order should be saved when no error occurred") {
                //Arrange
                every { ordersMock.findById(orderId) } returns order

                //Act
                orderService.returnOrder(command)

                //Assert
                verify { ordersMock.save(order, any()) }
            }

            test("Order occurred events should be published when no error occurred") {
                //Arrange
                every { ordersMock.findById(orderId) } returns order

                //Act
                orderService.returnOrder(command)

                //Assert
                verify { orderEventPublisherMock.publishBatch(order.occurredEvents()) }
            }
        }
    }
}