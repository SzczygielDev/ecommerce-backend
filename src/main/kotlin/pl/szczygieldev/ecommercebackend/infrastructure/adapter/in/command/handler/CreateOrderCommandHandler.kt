package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.command.handler

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.ecommercebackend.application.port.`in`.CartUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.OrderUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.CreateCartCommand
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.CreateOrderCommand
import pl.szczygieldev.ecommercebackend.domain.error.AppError

class CreateOrderCommandHandler(
    val orderUseCase: OrderUseCase,
    val cartUseCase: CartUseCase,
) : CommandWithResultHandler<CreateOrderCommand, Either<AppError, Unit>> {
    override suspend fun handle(command: CreateOrderCommand): Either<AppError, Unit> = either {
        orderUseCase.createOrder(command).bind()
        cartUseCase.createCart(CreateCartCommand()).bind()
    }
}