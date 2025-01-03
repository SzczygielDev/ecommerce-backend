package pl.szczygieldev.product.infrastructure.adapter.`in`.command.handler

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.ecommercelibrary.command.CommandError
import pl.szczygieldev.product.application.port.`in`.ProductUseCase
import pl.szczygieldev.product.application.port.`in`.command.UpdateProductCommand
import pl.szczygieldev.product.domain.error.AppError

internal class UpdateProductCommandHandler(val productUseCase: ProductUseCase) : CommandWithResultHandler<UpdateProductCommand, Either<AppError, Unit>> {
    override suspend fun handle(command: UpdateProductCommand): Either<AppError, Unit> = either{
        productUseCase.update(command).bind()
    }
}

