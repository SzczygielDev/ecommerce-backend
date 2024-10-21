package pl.szczygieldev.ecommercebackend.application

import arrow.core.raise.either
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.*
import pl.szczygieldev.ecommercebackend.application.handlers.BeginOrderPackingCommandHandler
import pl.szczygieldev.ecommercebackend.application.handlers.ChangeOrderDeliveryStatusCommandHandler
import pl.szczygieldev.ecommercebackend.application.handlers.CompleteOrderPackingCommandHandler
import pl.szczygieldev.ecommercebackend.application.handlers.common.CommandId
import pl.szczygieldev.ecommercebackend.application.model.OrderProjection
import pl.szczygieldev.ecommercebackend.application.model.PaymentProjection
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.BeginOrderPackingCommand
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.ChangeOrderDeliveryStatusCommand
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

class OrderShippingUseCaseTests : FunSpec() {
    val ordersMock = mockk<Orders>()
    val orderEventPublisherMock = mockk<DomainEventPublisher<OrderEvent>>()
    val shippingService = mockk<ShippingService>()
    val orderProjections = mockk<OrdersProjections>()

    val orderShippingUseCase = OrderShippingService(
        ordersMock,
        orderEventPublisherMock,
        shippingService,
        orderProjections
    )

    init {
        isolationMode = IsolationMode.InstancePerLeaf

        every { ordersMock.save(any(), any()) } just runs
        every { orderEventPublisherMock.publish(any()) } just runs
        every { orderEventPublisherMock.publishBatch(any()) } just runs

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
        val imageId = ImageId(UUID.randomUUID().toString())

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
                    1, imageId
                )
            )
        )

        context("Begin packing") {
            val command = BeginOrderPackingCommand(CommandId(), orderId)

            test("OrderNotFoundError should be raised when order not found") {
                //Arrange
                every { ordersMock.findById(any()) } returns null

                //Act
                val result = orderShippingUseCase.beginPacking(command)

                //Assert
                result.isLeft().shouldBe(true)
                val error = result.leftOrNull().shouldNotBeNull()
                error.shouldBeInstanceOf<OrderNotFoundError>()
            }

            test("Order packing begin should be called") {
                //Arrange
                val order = spyk(order)
                every { ordersMock.findById(orderId) } returns order

                //Act
                orderShippingUseCase.beginPacking(command)

                //Assert
                verify { order.beginPacking() }
            }

            test("Order should be saved when no error occurred") {
                //Arrange
                every { ordersMock.findById(orderId) } returns order

                //Act
                orderShippingUseCase.beginPacking(command)

                //Assert
                verify { ordersMock.save(order, any()) }
            }

            test("Order occurred events should be published when no error occurred") {
                //Arrange
                every { ordersMock.findById(orderId) } returns order

                //Act
                orderShippingUseCase.beginPacking(command)

                //Assert
                verify { orderEventPublisherMock.publishBatch(order.occurredEvents()) }
            }
        }

        context("Complete packing") {
            val command = CompleteOrderPackingCommand(CommandId(), orderId, dimensions)
            order.beginPacking()

            test("OrderNotFoundError should be raised when order was not found") {
                //Arrange
                every { ordersMock.findById(orderId) } returns null

                //Act
                val result = orderShippingUseCase.completePacking(command)

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
                val result = orderShippingUseCase.completePacking(command)

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
                orderShippingUseCase.completePacking(command)

                //Assert
                verify { shippingService.registerParcel(dimensions, deliveryProvider) }
            }

            test("CannotRegisterParcelError should be raised when ShippingService returns null") {
                //Arrange
                every { ordersMock.findById(orderId) } returns order
                every { orderProjections.findById(orderId) } returns orderProjection
                every { shippingService.registerParcel(any(), any()) } returns null

                //Act
                val result = orderShippingUseCase.completePacking(command)

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
                orderShippingUseCase.completePacking(command)

                //Assert
                verify { order.completePacking(parcelId, dimensions) }
            }

            test("Order should be saved when no error occurred") {
                //Arrange
                every { ordersMock.findById(orderId) } returns order
                every { orderProjections.findById(orderId) } returns orderProjection
                every { shippingService.registerParcel(any(), any()) } returns parcelId

                //Act
                orderShippingUseCase.completePacking(command)

                //Assert
                verify { ordersMock.save(order, any()) }
            }

            test("Order occurred events should be published when no error occurred") {
                //Arrange
                every { ordersMock.findById(orderId) } returns order
                every { orderProjections.findById(orderId) } returns orderProjection
                every { shippingService.registerParcel(any(), any()) } returns parcelId

                //Act
                orderShippingUseCase.completePacking(command)

                //Assert
                verify { orderEventPublisherMock.publishBatch(order.occurredEvents()) }
            }
        }

        context("Change delivery status") {
            val deliveryStatus = DeliveryStatus.IN_DELIVERY
            val command = ChangeOrderDeliveryStatusCommand(parcelId, deliveryStatus)

            test("OrderNotFoundError should be raised when order projection was not found") {
                //Arrange
                every { orderProjections.findByParcelIdentifier(parcelId) } returns null

                //Act
                val result = orderShippingUseCase.changeDeliveryStatus(command)

                //Assert
                result.isLeft().shouldBe(true)
                val error = result.leftOrNull().shouldNotBeNull()
                error.shouldBeInstanceOf<OrderNotFoundError>()
            }

            test("OrderNotFoundError should be raised when order was not found") {
                //Arrange
                every { orderProjections.findByParcelIdentifier(parcelId) } returns orderProjection
                every { ordersMock.findById(orderId) } returns null

                //Act
                val result = orderShippingUseCase.changeDeliveryStatus(command)

                //Assert
                result.isLeft().shouldBe(true)
                val error = result.leftOrNull().shouldNotBeNull()
                error.shouldBeInstanceOf<OrderNotFoundError>()
            }

            test("Order delivery status change should be called") {
                //Arrange
                val order = spyk(order)
                every { orderProjections.findByParcelIdentifier(parcelId) } returns orderProjection
                every { ordersMock.findById(orderId) } returns order

                //Act
                orderShippingUseCase.changeDeliveryStatus(command)

                //Assert
                verify { order.changeDeliveryStatus(deliveryStatus) }
            }

            test("Order should be saved when no error occurred") {
                //Arrange
                every { orderProjections.findByParcelIdentifier(parcelId) } returns orderProjection
                every { ordersMock.findById(orderId) } returns order

                //Act
                orderShippingUseCase.changeDeliveryStatus(command)

                //Assert
                verify { ordersMock.save(order, any()) }
            }

            test("Order occurred events should be published when no error occurred") {
                //Arrange
                every { orderProjections.findByParcelIdentifier(parcelId) } returns orderProjection
                every { ordersMock.findById(orderId) } returns order

                //Act
                orderShippingUseCase.changeDeliveryStatus(command)

                //Assert
                verify { orderEventPublisherMock.publishBatch(order.occurredEvents()) }
            }
        }

    }
}