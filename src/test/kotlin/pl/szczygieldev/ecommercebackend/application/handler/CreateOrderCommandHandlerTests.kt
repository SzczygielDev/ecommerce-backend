package pl.szczygieldev.ecommercebackend.application.handler

import arrow.core.raise.either
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import pl.szczygieldev.ecommercebackend.application.handlers.CreateOrderCommandHandler
import pl.szczygieldev.ecommercebackend.application.port.`in`.CartUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.OrderUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.CreateOrderCommand
import pl.szczygieldev.ecommercebackend.application.port.out.CommandResultStorage
import pl.szczygieldev.ecommercebackend.domain.CartId
import pl.szczygieldev.ecommercebackend.domain.DeliveryProvider
import pl.szczygieldev.ecommercebackend.domain.PaymentServiceProvider
import pl.szczygieldev.ecommercebackend.domain.error.AppError

class CreateOrderCommandHandlerTests : FunSpec() {
    val orderUseCaseMock = mockk<OrderUseCase>();
    val cartUseCaseMock = mockk<CartUseCase>();
    val commandResultStorage: CommandResultStorage = mockk<CommandResultStorage>()
    var commandHandler  =CreateOrderCommandHandler(orderUseCaseMock,cartUseCaseMock,commandResultStorage)
    init {
        isolationMode = IsolationMode.InstancePerLeaf

        coEvery { commandResultStorage.commandBegin(any()) } returns either { }
        coEvery { commandResultStorage.commandSuccess(any()) } returns either { }
        coEvery { commandResultStorage.commandFailed(any(), any<AppError>()) } returns either { }
        coEvery { commandResultStorage.commandFailed(any(), any<List<AppError>>()) } returns either { }

        coEvery { orderUseCaseMock.createOrder(any()) } returns either {  }
        coEvery { cartUseCaseMock.createCart(any()) } returns either {  }

        test("createOrder and createCart should be called"){
            //Arrange
            val command = CreateOrderCommand(CartId(""),PaymentServiceProvider.MOCK_PSP,DeliveryProvider.MOCK_DELIVERY_PROVIDER)

            //Act
            commandHandler.execute(command)

            //Assert
            coVerify { orderUseCaseMock.createOrder(any()) }
            coVerify { cartUseCaseMock.createCart(any())}
        }
    }
}