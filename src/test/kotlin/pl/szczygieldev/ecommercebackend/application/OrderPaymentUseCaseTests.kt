package pl.szczygieldev.ecommercebackend.application

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.*
import pl.szczygieldev.ecommercebackend.application.port.`in`.query.model.OrderProjection
import pl.szczygieldev.ecommercebackend.application.port.`in`.query.model.PaymentProjection
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.ProcessPaymentCommand
import pl.szczygieldev.ecommercebackend.application.port.out.Orders
import pl.szczygieldev.ecommercebackend.application.port.out.OrdersProjections
import pl.szczygieldev.ecommercebackend.application.port.out.PaymentService
import pl.szczygieldev.ecommercebackend.domain.*
import pl.szczygieldev.ecommercebackend.domain.error.OrderNotFoundError
import pl.szczygieldev.ecommercebackend.domain.event.OrderEvent
import pl.szczygieldev.shared.ddd.core.DomainEventPublisher
import java.math.BigDecimal
import java.net.URL
import java.time.Instant
import java.util.UUID

class OrderPaymentUseCaseTests : FunSpec() {
    val ordersMock = mockk<Orders>()
    val ordersProjections = mockk<OrdersProjections>()
    val orderEventPublisherMock = mockk<DomainEventPublisher<OrderEvent>>()
    val paymentServiceMock = mockk<PaymentService>()
    val orderPaymentService = OrderPaymentService(ordersProjections, ordersMock, orderEventPublisherMock,paymentServiceMock)

    init {
        isolationMode = IsolationMode.InstancePerLeaf

        val paymentId = PaymentId(UUID.randomUUID().toString())
        val paymentTransactionId = PaymentTransactionId(UUID.randomUUID().toString())

        val orderId = OrderId(UUID.randomUUID().toString())
        val amount = BigDecimal.TEN
        val psp = PaymentServiceProvider.MOCK_PSP
        val deliveryProvider = DeliveryProvider.MOCK_DELIVERY_PROVIDER
        val cartId = CartId(UUID.randomUUID().toString())

        val paymentUrl = URL("http://localhost:3000/")
        val imageId = ImageId(UUID.randomUUID().toString())

        val order = Order.create(
            orderId,
            cartId,
            PaymentDetails(
                paymentId, amount, paymentUrl, psp
            ),
            deliveryProvider,
            emptyList()
        )

        val orderProjection = OrderProjection(
            orderId,
            cartId,
            order.status,
            PaymentProjection(
                paymentId,
                amount,
                BigDecimal.ZERO,
                PaymentServiceProvider.MOCK_PSP,
                PaymentStatus.UNPAID,
                paymentUrl,
                emptyList()
            ),
            Delivery(deliveryProvider, DeliveryStatus.WAITING, null),
            Instant.now(),
            listOf(
                OrderProjection.OrderItemProjection(
                    ProductId(
                        UUID.randomUUID().toString(),
                    ),
                    "",
                    amount,
                    1, imageId
                )
            )
        )

        val paymentTransaction = PaymentTransaction(paymentTransactionId, amount, Instant.now())

        val orderSlot = slot<Order>()
        every { ordersMock.save(capture(orderSlot), any()) } just runs
        every { orderEventPublisherMock.publishBatch(any()) } just runs
        every { paymentServiceMock.verifyPayment(paymentId) } just runs

        test("OrderNotFoundError should be raised when OrderProjection for provided payment id was not found") {
            //Arrange
            every { ordersProjections.findByPaymentId(paymentId) } returns null
            val command = ProcessPaymentCommand(paymentId, paymentTransaction)

            //Act
            val result = orderPaymentService.pay(command)

            //Assert
            result.isLeft().shouldBe(true)
            val error = result.leftOrNull().shouldNotBeNull()
            error.shouldBeInstanceOf<OrderNotFoundError>()
        }

        test("OrderNotFoundError should be raised when Order for provided id was not found") {
            //Arrange
            every { ordersProjections.findByPaymentId(paymentId) } returns orderProjection
            every { ordersMock.findById(orderId) } returns null
            val command = ProcessPaymentCommand(paymentId, paymentTransaction)

            //Act
            val result = orderPaymentService.pay(command)

            //Assert
            result.isLeft().shouldBe(true)
            val error = result.leftOrNull().shouldNotBeNull()
            error.shouldBeInstanceOf<OrderNotFoundError>()
        }

        test("Order should register payment transaction") {
            //Arrange
            every { ordersProjections.findByPaymentId(paymentId) } returns orderProjection
            val order = spyk(order)
            every { ordersMock.findById(orderId) } returns order
            val command = ProcessPaymentCommand(paymentId, paymentTransaction)

            //Act
            orderPaymentService.pay(command)

            //Assert
            verify { order.pay(paymentTransaction) }
        }

        test("Payment verification should be called"){
            //Arrange
            every { ordersProjections.findByPaymentId(paymentId) } returns orderProjection
            every { ordersMock.findById(orderId) } returns order
            val command = ProcessPaymentCommand(paymentId, paymentTransaction)

            //Act
            orderPaymentService.pay(command)

            //Assert
            verify { paymentServiceMock.verifyPayment(paymentId) }
        }

        test("Order should be saved when no error occurred") {
            //Arrange
            every { ordersProjections.findByPaymentId(paymentId) } returns orderProjection
            every { ordersMock.findById(orderId) } returns order
            val command = ProcessPaymentCommand(paymentId, paymentTransaction)

            //Act
            orderPaymentService.pay(command)

            //Assert
            verify { ordersMock.save(order, any()) }
        }

        test("Order occurred events should be published when no error occurred") {
            //Arrange
            every { ordersProjections.findByPaymentId(paymentId) } returns orderProjection
            every { ordersMock.findById(orderId) } returns order
            val command = ProcessPaymentCommand(paymentId, paymentTransaction)

            //Act
            orderPaymentService.pay(command)

            //Assert
            verify { orderEventPublisherMock.publishBatch(order.occurredEvents()) }
        }
    }
}