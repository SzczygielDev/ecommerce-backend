package pl.szczygieldev.ecommercebackend.application.port.`in`

import pl.szczygieldev.ecommercebackend.application.command.AddItemToCartCommand
import pl.szczygieldev.ecommercebackend.application.command.RemoveItemFromCartCommand
import pl.szczygieldev.ecommercebackend.application.command.SubmitCartCommand

interface CartUseCase {
    fun submitCart(command: SubmitCartCommand)
    fun addProductToCart(command: AddItemToCartCommand)
    fun removeProductFromCart(command: RemoveItemFromCartCommand)
}