package pl.szczygieldev.ecommercebackend.application.port.`in`.command

import pl.szczygieldev.ecommercebackend.domain.*

data class UpdateProductCommand(
    val productId: ProductId,
    val title: ProductTitle,
    val description: ProductDescription,
    val price: ProductPrice,
    val imageId: ImageId
)