package pl.szczygieldev.product.infrastructure.adapter.`in`.command.handler

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.ecommercelibrary.command.CommandError
import pl.szczygieldev.product.application.port.`in`.ProductUseCase
import pl.szczygieldev.product.application.port.`in`.command.CreateProductCommand
import pl.szczygieldev.product.domain.error.AppError

internal class CreateProductCommandHandler(val productUseCase: ProductUseCase) :
    CommandWithResultHandler<CreateProductCommand, Either<AppError, Unit>> {
    override suspend fun handle(command: CreateProductCommand): Either<AppError, Unit> = either {
        productUseCase.create(command)
    }
}

