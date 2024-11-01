package pl.szczygieldev.ecommercebackend.application.port.`in`.command

import pl.szczygieldev.ecommercebackend.application.port.`in`.command.common.Command
import pl.szczygieldev.ecommercebackend.domain.ImageId
import pl.szczygieldev.ecommercebackend.domain.ProductId

data class CreateProductCommand(
    val productId: ProductId,
    val title: String,
    val description: String,
    val price: Double,
    val imageId: ImageId
) : Command() {
    init {
        require(price > 0) { "Product price must be positive value, provided='$price'" }
    }
}