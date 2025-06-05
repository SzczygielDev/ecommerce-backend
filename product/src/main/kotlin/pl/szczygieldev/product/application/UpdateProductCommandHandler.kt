package pl.szczygieldev.product.application

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.ecommercelibrary.ddd.core.DomainEventPublisher
import pl.szczygieldev.product.application.port.`in`.command.UpdateProductCommand
import pl.szczygieldev.product.application.port.out.Products
import pl.szczygieldev.product.domain.event.ProductEvent

internal class UpdateProductCommandHandler(
    val products: Products,
    val productEventPublisher: DomainEventPublisher<ProductEvent>
) : CommandWithResultHandler<UpdateProductCommand, Either<UpdateProductCommand.Error, Unit>> {
    override suspend fun handle(command: UpdateProductCommand): Either<UpdateProductCommand.Error, Unit> = either {
        val productId = command.productId
        val product = products.findById(productId) ?: raise(UpdateProductCommand.ProductNotFoundError(productId.id.toString()))

        val version = product.version

        product.title = command.title
        product.description = command.description
        product.updatePrice(command.price)
        product.imageId = command.imageId

        products.save(product, version)
        productEventPublisher.publishBatch(product.occurredEvents())
    }
}

