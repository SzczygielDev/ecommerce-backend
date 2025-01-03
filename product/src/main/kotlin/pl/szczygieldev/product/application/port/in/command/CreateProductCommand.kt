package pl.szczygieldev.product.application.port.`in`.command

import pl.szczygieldev.ecommercelibrary.command.Command
import pl.szczygieldev.product.domain.ImageId
import pl.szczygieldev.product.domain.ProductId
import pl.szczygieldev.product.domain.error.AppError

internal data class CreateProductCommand(
    val productId: ProductId,
    val title: String,
    val description: String,
    val price: Double,
    val imageId: ImageId
) : Command<AppError>() {
    init {
        require(price > 0) { "Product price must be positive value, provided='$price'" }
    }
}