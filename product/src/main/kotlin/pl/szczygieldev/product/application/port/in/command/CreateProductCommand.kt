package pl.szczygieldev.product.application.port.`in`.command

import pl.szczygieldev.ecommercelibrary.command.Command
import pl.szczygieldev.ecommercelibrary.command.CommandError
import pl.szczygieldev.product.domain.ImageId
import pl.szczygieldev.product.domain.ProductId

internal data class CreateProductCommand(
    val productId: ProductId,
    val title: String,
    val description: String,
    val price: Double,
    val imageId: ImageId
) : Command<CreateProductCommand.Error>() {
    init {
        require(price > 0) { "Product price must be positive value, provided='$price'" }
    }

    internal sealed class Error(override val message: String, override val code: String) :
        CommandError(message, code)
}