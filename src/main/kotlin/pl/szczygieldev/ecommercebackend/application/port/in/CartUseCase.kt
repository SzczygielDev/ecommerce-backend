package pl.szczygieldev.ecommercebackend.application.port.`in`

import arrow.core.Either
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.AddItemToCartCommand
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.RemoveItemFromCartCommand
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.SubmitCartCommand
import pl.szczygieldev.ecommercebackend.domain.error.AppError

interface CartUseCase {
    fun submitCart(command: SubmitCartCommand): Either<AppError, Unit>
    fun addProductToCart(command: AddItemToCartCommand): Either<AppError, Unit>
    fun removeProductFromCart(command: RemoveItemFromCartCommand): Either<AppError, Unit>
}