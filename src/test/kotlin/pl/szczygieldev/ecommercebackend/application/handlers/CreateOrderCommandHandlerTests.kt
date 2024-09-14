package pl.szczygieldev.ecommercebackend.application.handlers

import arrow.core.raise.either
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.*
import pl.szczygieldev.ecommercebackend.application.model.CartProjection
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.CreateOrderCommand
import pl.szczygieldev.ecommercebackend.application.port.out.*
import pl.szczygieldev.ecommercebackend.domain.*
import pl.szczygieldev.ecommercebackend.domain.error.AppError
import pl.szczygieldev.ecommercebackend.domain.error.CartNotFoundError
import pl.szczygieldev.ecommercebackend.domain.event.OrderEvent
import pl.szczygieldev.shared.ddd.core.DomainEventPublisher
import java.math.BigDecimal
import java.net.URL
import java.util.*

class CreateOrderCommandHandlerTests : FunSpec() {
    val ordersMock = mockk<Orders>()
    val cartsProjectionsMock = mockk<CartsProjections>()
    val orderEventPublisherMock = mockk<DomainEventPublisher<OrderEvent>>()
    val commandResultStorage: CommandResultStorage = mockk<CommandResultStorage>()
    val paymentServiceMock = mockk<PaymentService>()

    val createOrderCommandHandler: CreateOrderCommandHandler = spyk(
        CreateOrderCommandHandler(
            ordersMock,
            cartsProjectionsMock,
            paymentServiceMock,
            orderEventPublisherMock,
            commandResultStorage
        )
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

        val cartItemQuantity = 1
        val cartEntry = CartProjection.Entry(ProductId(UUID.randomUUID().toString()), cartItemQuantity)
        val cartProjection = CartProjection(cartId, CartStatus.SUBMITTED, amount, listOf(cartEntry))

        every { ordersMock.nextIdentity() } returns orderId
        val orderSlot = slot<Order>()
        every { ordersMock.save(capture(orderSlot), any()) } just runs
        every { paymentServiceMock.registerPayment(any(), any(), any()) } returns paymentRegistration
        every { cartsProjectionsMock.findById(cartId) } returns cartProjection
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
}