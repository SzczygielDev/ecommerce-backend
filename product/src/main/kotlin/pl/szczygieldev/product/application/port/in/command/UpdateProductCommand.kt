package pl.szczygieldev.product.application.port.`in`.command

import pl.szczygieldev.ecommercelibrary.command.Command
import pl.szczygieldev.product.domain.*
import pl.szczygieldev.product.domain.error.AppError

data class UpdateProductCommand(
    val productId: ProductId,
    val title: ProductTitle,
    val description: ProductDescription,
    val price: ProductPrice,
    val imageId: ImageId
) : Command<AppError>()