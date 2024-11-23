package pl.szczygieldev.product.application.port.`in`.command

import pl.szczygieldev.product.domain.*
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.common.Command

data class UpdateProductCommand(
    val productId: ProductId,
    val title: ProductTitle,
    val description: ProductDescription,
    val price: ProductPrice,
    val imageId: ImageId
) : Command()