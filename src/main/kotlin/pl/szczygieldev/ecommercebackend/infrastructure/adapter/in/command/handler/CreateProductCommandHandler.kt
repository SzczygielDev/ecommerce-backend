package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.command.handler

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.ecommercebackend.application.port.`in`.ProductUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.CreateProductCommand
import pl.szczygieldev.ecommercebackend.domain.error.AppError

class CreateProductCommandHandler(val productUseCase: ProductUseCase) :
    CommandWithResultHandler<CreateProductCommand, Either<AppError, Unit>> {
    override suspend fun handle(command: CreateProductCommand): Either<AppError, Unit> = either {
        productUseCase.create(command)
    }
}

