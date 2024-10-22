package pl.szczygieldev.ecommercebackend.application.handlers

import arrow.core.Either
import arrow.core.raise.either
import pl.szczygieldev.ecommercebackend.application.handlers.common.CommandHandler
import pl.szczygieldev.ecommercebackend.application.port.`in`.CartUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.OrderUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.CreateCartCommand
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.CreateOrderCommand
import pl.szczygieldev.ecommercebackend.application.port.out.CommandResultStorage
import pl.szczygieldev.ecommercebackend.domain.error.AppError

class CreateOrderCommandHandler(
    val orderUseCase: OrderUseCase,
    val cartUseCase: CartUseCase,
    commandResultStorage: CommandResultStorage
) : CommandHandler<CreateOrderCommand>(commandResultStorage) {

    override suspend fun processCommand(command: CreateOrderCommand): Either<AppError, Unit> = either {
       orderUseCase.createOrder(command).bind()
       cartUseCase.createCart(CreateCartCommand()).bind()
    }
}