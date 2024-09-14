package pl.szczygieldev.ecommercebackend.application.handlers

import arrow.core.raise.either
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.*
import pl.szczygieldev.ecommercebackend.application.handlers.common.CommandId
import pl.szczygieldev.ecommercebackend.application.model.OrderProjection
import pl.szczygieldev.ecommercebackend.application.model.PaymentProjection
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.CompleteOrderPackingCommand
import pl.szczygieldev.ecommercebackend.application.port.out.CommandResultStorage
import pl.szczygieldev.ecommercebackend.application.port.out.Orders
import pl.szczygieldev.ecommercebackend.application.port.out.OrdersProjections
import pl.szczygieldev.ecommercebackend.application.port.out.ShippingService
import pl.szczygieldev.ecommercebackend.domain.*
import pl.szczygieldev.ecommercebackend.domain.error.AppError
import pl.szczygieldev.ecommercebackend.domain.error.CannotRegisterParcelError
import pl.szczygieldev.ecommercebackend.domain.error.OrderNotFoundError
import pl.szczygieldev.ecommercebackend.domain.event.OrderEvent
import pl.szczygieldev.shared.ddd.core.DomainEventPublisher
import java.math.BigDecimal
import java.net.URL
import java.time.Instant
import java.util.*

class CompleteOrderPackingCommandHandlerTests : FunSpec() {
    val ordersMock = mockk<Orders>()
    val orderProjections = mockk<OrdersProjections>()
    val orderEventPublisherMock = mockk<DomainEventPublisher<OrderEvent>>()
    val shippingService = mockk<ShippingService>()
    val commandResultStorage: CommandResultStorage = mockk<CommandResultStorage>()

    val completeOrderPackingCommandHandler =
        spyk(
            CompleteOrderPackingCommandHandler(
                ordersMock,
                orderEventPublisherMock,
                shippingService,
                orderProjections,
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
        order.accept()

        val paymentTransaction =
            PaymentTransaction(PaymentTransactionId(UUID.randomUUID().toString()), amount, Instant.now())
        order.pay(paymentTransaction)

        val dimensions = ParcelDimensions(10.0, 25.0, 30.0, 5.0)
        val parcelId = ParcelId(UUID.randomUUID().toString())

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
                    ProductTitle(""),
                    ProductPrice(amount),
                    1
                )
            )
        )

        val command = CompleteOrderPackingCommand(CommandId(),orderId, dimensions)
        order.beginPacking()

        test("OrderNotFoundError should be raised when order was not found") {
            //Arrange
            every { ordersMock.findById(orderId) } returns null

            //Act
            val result = completeOrderPackingCommandHandler.execute(command)

            //Assert
            result.isLeft().shouldBe(true)
            val error = result.leftOrNull().shouldNotBeNull()
            error.shouldBeInstanceOf<OrderNotFoundError>()
        }

        test("OrderNotFoundError should be raised when order projection was not found") {
            //Arrange
            every { ordersMock.findById(orderId) } returns order
            every { orderProjections.findById(orderId) } returns null

            //Act
            val result = completeOrderPackingCommandHandler.execute(command)

            //Assert
            result.isLeft().shouldBe(true)
            val error = result.leftOrNull().shouldNotBeNull()
            error.shouldBeInstanceOf<OrderNotFoundError>()
        }

        test("Parcel should be registered for order") {
            //Arrange
            every { ordersMock.findById(orderId) } returns order
            every { orderProjections.findById(orderId) } returns orderProjection
            every { shippingService.registerParcel(any(), any()) } returns parcelId

            //Act
            completeOrderPackingCommandHandler.execute(command)

            //Assert
            verify { shippingService.registerParcel(dimensions, deliveryProvider) }
        }

        test("CannotRegisterParcelError should be raised when ShippingService returns null") {
            //Arrange
            every { ordersMock.findById(orderId) } returns order
            every { orderProjections.findById(orderId) } returns orderProjection
            every { shippingService.registerParcel(any(), any()) } returns null

            //Act
            val result = completeOrderPackingCommandHandler.execute(command)

            //Assert
            result.isLeft().shouldBe(true)
            val error = result.leftOrNull().shouldNotBeNull()
            error.shouldBeInstanceOf<CannotRegisterParcelError>()
        }

        test("Order packing completion should be called") {
            //Arrange
            val order = spyk(order)
            every { ordersMock.findById(orderId) } returns order
            every { orderProjections.findById(orderId) } returns orderProjection
            every { shippingService.registerParcel(any(), any()) } returns parcelId

            //Act
            completeOrderPackingCommandHandler.execute(command)

            //Assert
            verify { order.completePacking(parcelId, dimensions) }
        }

        test("Order should be saved when no error occurred") {
            //Arrange
            every { ordersMock.findById(orderId) } returns order
            every { orderProjections.findById(orderId) } returns orderProjection
            every { shippingService.registerParcel(any(), any()) } returns parcelId

            //Act
            completeOrderPackingCommandHandler.execute(command)

            //Assert
            verify { ordersMock.save(order, any()) }
        }

        test("Order occurred events should be published when no error occurred") {
            //Arrange
            every { ordersMock.findById(orderId) } returns order
            every { orderProjections.findById(orderId) } returns orderProjection
            every { shippingService.registerParcel(any(), any()) } returns parcelId

            //Act
            completeOrderPackingCommandHandler.execute(command)

            //Assert
            verify { orderEventPublisherMock.publishBatch(order.occurredEvents()) }
        }
    }
}