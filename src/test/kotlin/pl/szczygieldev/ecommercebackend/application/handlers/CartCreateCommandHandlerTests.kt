package pl.szczygieldev.ecommercebackend.application.handlers

import arrow.core.raise.either
import io.kotest.core.spec.style.FunSpec
import io.mockk.*
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.CreateCartCommand
import pl.szczygieldev.ecommercebackend.application.port.out.Carts
import pl.szczygieldev.ecommercebackend.application.port.out.CommandResultStorage
import pl.szczygieldev.ecommercebackend.domain.*
import pl.szczygieldev.ecommercebackend.domain.error.AppError
import pl.szczygieldev.ecommercebackend.domain.event.CartEvent
import pl.szczygieldev.shared.ddd.core.DomainEventPublisher
import java.math.BigDecimal
import java.util.*

class CartCreateCommandHandlerTests : FunSpec() {
    val cartsMock = mockk<Carts>()
    val eventPublisherMock = mockk<DomainEventPublisher<CartEvent>>()
    val commandResultStorage: CommandResultStorage = mockk<CommandResultStorage>()
    val cartCreateCommandHandler = CartCreateCommandHandler(cartsMock, eventPublisherMock, commandResultStorage)

    init {
        every { cartsMock.save(any(), any()) } just runs
        every { eventPublisherMock.publish(any()) } just runs
        every { eventPublisherMock.publishBatch(any()) } just runs
        coEvery { commandResultStorage.commandBegin(any()) } returns either { }
        coEvery { commandResultStorage.commandSuccess(any()) } returns either { }
        coEvery { commandResultStorage.commandFailed(any(), any<AppError>()) } returns either { }
        coEvery { commandResultStorage.commandFailed(any(), any<List<AppError>>()) } returns either { }

        test("Cart should be saved when no error occurred") {
            //Arrange
            val command = CreateCartCommand()
            val cartId = CartId(UUID.randomUUID().toString())
            every { cartsMock.nextIdentity() } returns cartId

            //Act
            cartCreateCommandHandler.execute(command)

            //Assert
            verify { cartsMock.save(any(), any()) }
        }

        test("Cart occurred events should be published when no error occurred"){
            //Arrange
            val command = CreateCartCommand()
            val cartId = CartId(UUID.randomUUID().toString())
            every { cartsMock.nextIdentity() } returns cartId

            //Act
            cartCreateCommandHandler.execute(command)

            //Assert
            verify { eventPublisherMock.publishBatch(any()) }
        }
    }
}