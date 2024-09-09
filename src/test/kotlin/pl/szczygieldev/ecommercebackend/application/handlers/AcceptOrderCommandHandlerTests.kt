package pl.szczygieldev.ecommercebackend.application.handlers

import arrow.core.raise.either
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.*
import pl.szczygieldev.ecommercebackend.application.handlers.common.CommandId
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.AcceptOrderCommand
import pl.szczygieldev.ecommercebackend.application.port.out.*
import pl.szczygieldev.ecommercebackend.domain.*
import pl.szczygieldev.ecommercebackend.domain.error.AppError
import pl.szczygieldev.ecommercebackend.domain.error.OrderNotFoundError
import pl.szczygieldev.ecommercebackend.domain.event.OrderEvent
import pl.szczygieldev.shared.ddd.core.DomainEventPublisher
import java.math.BigDecimal
import java.net.URL
import java.util.*

class AcceptOrderCommandHandlerTests : FunSpec() {
    val ordersMock = mockk<Orders>()
    val orderEventPublisherMock = mockk<DomainEventPublisher<OrderEvent>>()
    val commandResultStorage: CommandResultStorage = mockk<CommandResultStorage>()
    val acceptOrderCommandHandler: AcceptOrderCommandHandler =
        spyk(AcceptOrderCommandHandler(ordersMock, orderEventPublisherMock, commandResultStorage))

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


        every { ordersMock.nextIdentity() } returns orderId
        val orderSlot = slot<Order>()
        every { ordersMock.save(capture(orderSlot), any()) } just runs


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
}