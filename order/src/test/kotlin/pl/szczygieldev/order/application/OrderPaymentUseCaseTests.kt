package pl.szczygieldev.order.application

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.*
import pl.szczygieldev.ecommercelibrary.ddd.core.DomainEventPublisher
import pl.szczygieldev.order.application.port.`in`.command.ProcessPaymentCommand
import pl.szczygieldev.order.application.port.out.Orders
import pl.szczygieldev.order.application.port.out.PaymentService
import pl.szczygieldev.order.domain.*
import pl.szczygieldev.order.domain.error.OrderNotFoundError
import pl.szczygieldev.order.domain.event.OrderEvent
import java.math.BigDecimal
import java.net.URL
import java.time.Instant
import java.util.UUID

internal class OrderPaymentUseCaseTests : FunSpec() {
    val ordersMock = mockk<Orders>()
    val orderEventPublisherMock = mockk<DomainEventPublisher<OrderEvent>>()
    val paymentServiceMock = mockk<PaymentService>()
    val orderPaymentService = OrderPaymentService(ordersMock, orderEventPublisherMock,paymentServiceMock)

    init {
        isolationMode = IsolationMode.InstancePerLeaf

        val paymentId = PaymentId(UUID.randomUUID())
        val paymentTransactionId = PaymentTransactionId(UUID.randomUUID())

        val orderId = OrderId(UUID.randomUUID())
        val amount = BigDecimal.TEN
        val psp = PaymentServiceProvider.MOCK_PSP
        val deliveryProvider = DeliveryProvider.MOCK_DELIVERY_PROVIDER
        val cartId = CartId(UUID.randomUUID())

        val paymentUrl = URL("http://localhost:3000/")
        val imageId = ImageId(UUID.randomUUID())

        val order = Order.create(
            orderId,
            cartId,
            PaymentDetails(
                paymentId, amount, paymentUrl, psp
            ),
            deliveryProvider,
            emptyList()
        )


        val paymentTransaction = PaymentTransaction(paymentTransactionId, amount, Instant.now())

        val orderSlot = slot<Order>()
        every { ordersMock.save(capture(orderSlot), any()) } just runs
        every { orderEventPublisherMock.publishBatch(any()) } just runs
        every { paymentServiceMock.verifyPayment(paymentId) } just runs
        every { ordersMock.findByPaymentId(paymentId) } returns order

        test("OrderNotFoundError should be raised when Order for provided id was not found") {
            //Arrange
            every { ordersMock.findByPaymentId(paymentId) } returns null

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
            val order = spyk(order)
            every { ordersMock.findByPaymentId(paymentId) } returns order
            val command = ProcessPaymentCommand(paymentId, paymentTransaction)

            //Act
            orderPaymentService.pay(command)

            //Assert
            verify { order.pay(paymentTransaction) }
        }

        test("Payment verification should be called"){
            //Arrange
            val command = ProcessPaymentCommand(paymentId, paymentTransaction)

            //Act
            orderPaymentService.pay(command)

            //Assert
            verify { paymentServiceMock.verifyPayment(paymentId) }
        }

        test("Order should be saved when no error occurred") {
            //Arrange
            val command = ProcessPaymentCommand(paymentId, paymentTransaction)

            //Act
            orderPaymentService.pay(command)

            //Assert
            verify { ordersMock.save(order, any()) }
        }

        test("Order occurred events should be published when no error occurred") {
            //Arrange
            val command = ProcessPaymentCommand(paymentId, paymentTransaction)

            //Act
            orderPaymentService.pay(command)

            //Assert
            verify { orderEventPublisherMock.publishBatch(order.occurredEvents()) }
        }
    }
}