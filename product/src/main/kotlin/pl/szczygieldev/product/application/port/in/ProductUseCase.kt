package pl.szczygieldev.product.application.port.`in`

import arrow.core.Either
import pl.szczygieldev.product.application.port.`in`.command.CreateProductCommand
import pl.szczygieldev.product.application.port.`in`.command.UpdateProductCommand
import pl.szczygieldev.product.domain.error.AppError

interface ProductUseCase {
    fun create(command: CreateProductCommand): Either<AppError, Unit>

    fun update(command: UpdateProductCommand): Either<AppError, Unit>
}