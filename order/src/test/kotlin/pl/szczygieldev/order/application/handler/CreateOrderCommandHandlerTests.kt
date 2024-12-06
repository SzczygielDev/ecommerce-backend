package pl.szczygieldev.order.application.handler

import arrow.core.raise.either
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import pl.szczygieldev.ecommercelibrary.command.CommandResultStorage
import pl.szczygieldev.order.infrastructure.adapter.`in`.command.handler.CreateOrderCommandHandler
import pl.szczygieldev.order.application.port.`in`.CartUseCase
import pl.szczygieldev.order.application.port.`in`.OrderUseCase
import pl.szczygieldev.order.application.port.`in`.command.CreateOrderCommand
import pl.szczygieldev.order.domain.CartId
import pl.szczygieldev.order.domain.DeliveryProvider
import pl.szczygieldev.order.domain.PaymentServiceProvider
import pl.szczygieldev.order.domain.error.AppError


class CreateOrderCommandHandlerTests : FunSpec() {
    val orderUseCaseMock = mockk<OrderUseCase>();
    val cartUseCaseMock = mockk<CartUseCase>();
    val commandResultStorage: CommandResultStorage = mockk<CommandResultStorage>()
    var commandHandler = CreateOrderCommandHandler(orderUseCaseMock, cartUseCaseMock)

    init {
        isolationMode = IsolationMode.InstancePerLeaf

        coEvery { commandResultStorage.commandBegin(any()) } returns either { }
        coEvery { commandResultStorage.commandSuccess(any()) } returns either { }
        coEvery { commandResultStorage.commandFailed(any(), any<AppError>()) } returns either { }
        coEvery { commandResultStorage.commandFailed(any(), any<List<AppError>>()) } returns either { }

        coEvery { orderUseCaseMock.createOrder(any()) } returns either { }
        coEvery { cartUseCaseMock.createCart(any()) } returns either { }

        test("createOrder and createCart should be called") {
            //Arrange
            val command =
                CreateOrderCommand(CartId(""), PaymentServiceProvider.MOCK_PSP, DeliveryProvider.MOCK_DELIVERY_PROVIDER)

            //Act
            commandHandler.handle(command)

            //Assert
            coVerify { orderUseCaseMock.createOrder(any()) }
            coVerify { cartUseCaseMock.createCart(any()) }
        }
    }
}