package pl.szczygieldev.product.application.port.`in`.command

import pl.szczygieldev.ecommercelibrary.command.Command
import pl.szczygieldev.ecommercelibrary.command.CommandError
import pl.szczygieldev.product.domain.*

internal data class UpdateProductCommand(
    val productId: ProductId,
    val title: ProductTitle,
    val description: ProductDescription,
    val price: ProductPrice,
    val imageId: ImageId
) : Command<UpdateProductCommand.Error>(){
    internal sealed class Error(override val message: String, override val code: String) :
        CommandError(message, code)

    internal data class ProductNotFoundError(override val message: String) : Error(message,"UP-1") {
        companion object {
            fun forId(id: ProductId): ProductNotFoundError {
                return ProductNotFoundError("Cannot find product with id='${id.id()}'")
            }
        }
    }
}