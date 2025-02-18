package pl.szczygieldev.cart.application.port.`in`

import arrow.core.Either
import pl.szczygieldev.cart.application.port.`in`.command.AddItemToCartCommand
import pl.szczygieldev.cart.application.port.`in`.command.CreateCartCommand
import pl.szczygieldev.cart.application.port.`in`.command.RemoveItemFromCartCommand
import pl.szczygieldev.cart.application.port.`in`.command.SubmitCartCommand
import pl.szczygieldev.cart.domain.AppError

internal interface CartUseCase {
    suspend fun createCart(command: CreateCartCommand): Either<AppError, Unit>
    fun submitCart(command: SubmitCartCommand): Either<AppError, Unit>
    fun addProductToCart(command: AddItemToCartCommand): Either<AppError, Unit>
    fun removeProductFromCart(command: RemoveItemFromCartCommand): Either<AppError, Unit>
}