package pl.szczygieldev.ecommercebackend.application

import arrow.core.raise.either
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
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
import pl.szczygieldev.ecommercebackend.domain.event.OrderEvent
import pl.szczygieldev.shared.ddd.core.DomainEventPublisher
import java.math.BigDecimal
import java.net.URL
import java.time.Instant
import java.util.*

class OrderShippingUseCaseTests : FunSpec() {
    val ordersMock = mockk<Orders>()
    val orderProjections = mockk<OrdersProjections>()
    val orderEventPublisherMock = mockk<DomainEventPublisher<OrderEvent>>()
    val shippingService = mockk<ShippingService>()
    val commandResultStorage: CommandResultStorage = mockk<CommandResultStorage>()

    val beginOrderPackingCommandHandler =
        spyk(BeginOrderPackingCommandHandler(ordersMock, orderEventPublisherMock, commandResultStorage))
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
    val changeOrderDeliveryStatusCommandHandler = spyk(
        ChangeOrderDeliveryStatusCommandHandler(
            ordersMock,
            orderEventPublisherMock,
            orderProjections,
            commandResultStorage
        )
    )

    val orderShippingUseCase = OrderShippingService(
        beginOrderPackingCommandHandler,
        completeOrderPackingCommandHandler,
        changeOrderDeliveryStatusCommandHandler
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

        test("Order packing begin should call BeginOrderPackingCommandHandler") {
            //Arrange
            every { ordersMock.findById(orderId) } returns order
            val command = BeginOrderPackingCommand(CommandId(),orderId)

            //Act
            orderShippingUseCase.beginPacking(command)

            //Assert
            coVerify { beginOrderPackingCommandHandler.execute(command) }
        }

        test("Order packing completion should call CompleteOrderPackingCommandHandler") {
            //Arrange
            every { ordersMock.findById(orderId) } returns order
            every { orderProjections.findById(orderId) } returns orderProjection
            every { shippingService.registerParcel(any(), any()) } returns parcelId
            val command = CompleteOrderPackingCommand(CommandId(),orderId, dimensions)
            order.beginPacking()

            //Act
            orderShippingUseCase.completePacking(command)

            //Assert
            coVerify { completeOrderPackingCommandHandler.execute(command) }
        }

        test("Order delivery status change should call ChangeOrderDeliveryStatusCommandHandler") {
            //Arrange
            every { orderProjections.findByParcelIdentifier(parcelId) } returns orderProjection
            every { ordersMock.findById(orderId) } returns order
            val deliveryStatus = DeliveryStatus.IN_DELIVERY
            val command = ChangeOrderDeliveryStatusCommand(parcelId, deliveryStatus)

            //Act
            orderShippingUseCase.changeDeliveryStatus(command)

            //Assert
            coVerify { changeOrderDeliveryStatusCommandHandler.execute(command) }
        }
    }
}