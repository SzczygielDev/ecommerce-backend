package pl.szczygieldev.ecommercebackend.application.handlers

import arrow.core.Either
import arrow.core.raise.either
import pl.szczygieldev.ecommercebackend.application.handlers.common.CommandHandler
import pl.szczygieldev.ecommercebackend.application.port.`in`.OrderUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.CreateOrderCommand
import pl.szczygieldev.ecommercebackend.application.port.out.CartsProjections
import pl.szczygieldev.ecommercebackend.application.port.out.CommandResultStorage
import pl.szczygieldev.ecommercebackend.application.port.out.Orders
import pl.szczygieldev.ecommercebackend.application.port.out.PaymentService
import pl.szczygieldev.ecommercebackend.domain.Order
import pl.szczygieldev.ecommercebackend.domain.PaymentDetails
import pl.szczygieldev.ecommercebackend.domain.error.AppError
import pl.szczygieldev.ecommercebackend.domain.error.CartNotFoundError
import pl.szczygieldev.ecommercebackend.domain.event.OrderEvent
import pl.szczygieldev.shared.ddd.core.DomainEventPublisher
import java.net.URL

class CreateOrderCommandHandler(
    val orderUseCase: OrderUseCase,
    commandResultStorage: CommandResultStorage
) : CommandHandler<CreateOrderCommand>(commandResultStorage) {


    override suspend fun processCommand(command: CreateOrderCommand): Either<AppError, Unit> = either {
        orderUseCase.createOrder(command).bind()
    }
}