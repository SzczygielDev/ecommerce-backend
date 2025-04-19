package pl.szczygieldev.cart.application.port.`in`

import arrow.core.Either
import pl.szczygieldev.cart.application.port.`in`.command.AddItemToCartCommand
import pl.szczygieldev.cart.application.port.`in`.command.CreateCartCommand
import pl.szczygieldev.cart.application.port.`in`.command.RemoveItemFromCartCommand
import pl.szczygieldev.cart.application.port.`in`.command.SubmitCartCommand

internal interface CartUseCase {
    suspend fun createCart(command: CreateCartCommand): Either<CreateCartCommand.Error, Unit>
    fun submitCart(command: SubmitCartCommand): Either<SubmitCartCommand.Error, Unit>
    fun addProductToCart(command: AddItemToCartCommand): Either<AddItemToCartCommand.Error, Unit>
    fun removeProductFromCart(command: RemoveItemFromCartCommand): Either<RemoveItemFromCartCommand.Error, Unit>
}