package pl.szczygieldev.product.application

import arrow.core.Either
import arrow.core.raise.either
import pl.szczygieldev.ecommercelibrary.architecture.UseCase
import pl.szczygieldev.ecommercelibrary.ddd.core.DomainEventPublisher
import pl.szczygieldev.product.application.port.`in`.ProductUseCase
import pl.szczygieldev.product.application.port.`in`.command.CreateProductCommand
import pl.szczygieldev.product.application.port.`in`.command.UpdateProductCommand
import pl.szczygieldev.product.application.port.out.Products
import pl.szczygieldev.product.domain.Product
import pl.szczygieldev.product.domain.ProductDescription
import pl.szczygieldev.product.domain.ProductPrice
import pl.szczygieldev.product.domain.ProductTitle
import pl.szczygieldev.product.domain.error.AppError
import pl.szczygieldev.product.domain.error.ProductNotFoundError
import pl.szczygieldev.product.domain.event.ProductEvent
import java.math.BigDecimal

@UseCase
internal class ProductService(val products: Products, val productEventPublisher: DomainEventPublisher<ProductEvent>) :
    ProductUseCase {
    override fun create(command: CreateProductCommand): Either<AppError, Unit> = either {
        val product = Product.create(
            command.productId,
            ProductTitle(command.title),
            ProductDescription(command.description),
            ProductPrice(BigDecimal.valueOf(command.price)),
            command.imageId
        )

        products.save(product, product.version)
    }

    override fun update(command: UpdateProductCommand): Either<AppError, Unit> = either {
        val productId = command.productId
        val product = products.findById(productId) ?: raise(ProductNotFoundError(productId.id))

        val version = product.version

        product.title = command.title
        product.description = command.description
        product.updatePrice(command.price)
        product.imageId = command.imageId

        products.save(product, version)
        productEventPublisher.publishBatch(product.occurredEvents())
    }
}