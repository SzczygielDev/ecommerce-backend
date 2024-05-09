package pl.szczygieldev.ecommercebackend.application.port.`in`

import pl.szczygieldev.ecommercebackend.application.port.`in`.command.AddItemToCartCommand
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.RemoveItemFromCartCommand
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.SubmitCartCommand

interface CartUseCase {
    fun submitCart(command: SubmitCartCommand)
    fun addProductToCart(command: AddItemToCartCommand)
    fun removeProductFromCart(command: RemoveItemFromCartCommand)
}