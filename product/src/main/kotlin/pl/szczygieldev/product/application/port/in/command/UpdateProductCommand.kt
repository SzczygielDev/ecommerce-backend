package pl.szczygieldev.product.application.port.`in`.command

import pl.szczygieldev.ecommercelibrary.command.Command
import pl.szczygieldev.product.domain.*
data class UpdateProductCommand(
    val productId: ProductId,
    val title: ProductTitle,
    val description: ProductDescription,
    val price: ProductPrice,
    val imageId: ImageId
) : Command()