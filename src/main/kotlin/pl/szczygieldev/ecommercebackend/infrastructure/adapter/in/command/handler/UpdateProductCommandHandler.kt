package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.command.handler

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.ecommercebackend.application.port.`in`.ProductUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.UpdateProductCommand
import pl.szczygieldev.ecommercebackend.domain.error.AppError

class UpdateProductCommandHandler(val productUseCase: ProductUseCase) : CommandWithResultHandler<UpdateProductCommand, Either<AppError, Unit>> {
    override suspend fun handle(command: UpdateProductCommand): Either<AppError, Unit> = either{
        productUseCase.update(command).bind()
    }
}

