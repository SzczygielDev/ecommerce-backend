package pl.szczygieldev.ecommercebackend.application.port.`in`

import arrow.core.Either
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.CreateProductCommand
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.UpdateProductCommand
import pl.szczygieldev.ecommercebackend.domain.Product
import pl.szczygieldev.ecommercebackend.domain.error.AppError

interface ProductUseCase {
    fun create(command: CreateProductCommand): Either<AppError, Unit>

    fun update(command: UpdateProductCommand): Either<AppError, Unit>
}