package pl.szczygieldev.ecommercebackend.application.handlers

import arrow.core.Either
import arrow.core.raise.either
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.ReturnOrderCommand
import pl.szczygieldev.ecommercebackend.application.port.out.Orders
import pl.szczygieldev.ecommercebackend.domain.error.AppError
import pl.szczygieldev.ecommercebackend.domain.error.OrderNotFoundError
import pl.szczygieldev.ecommercebackend.domain.event.OrderEvent
import pl.szczygieldev.ecommercebackend.application.handlers.common.CommandHandler
import pl.szczygieldev.ecommercebackend.application.port.`in`.OrderUseCase
import pl.szczygieldev.ecommercebackend.application.port.out.CommandResultStorage
import pl.szczygieldev.shared.ddd.core.DomainEventPublisher

class ReturnOrderCommandHandler(
    val orderUseCase: OrderUseCase,
    commandResultStorage: CommandResultStorage
) :
    CommandHandler<ReturnOrderCommand>(commandResultStorage) {

    override suspend fun processCommand(command: ReturnOrderCommand): Either<AppError, Unit> = either {
        orderUseCase.returnOrder(command).bind()
    }
}