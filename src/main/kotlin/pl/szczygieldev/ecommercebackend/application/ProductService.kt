package pl.szczygieldev.ecommercebackend.application

import arrow.core.Either
import arrow.core.raise.either
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.CreateProductCommand
import pl.szczygieldev.ecommercebackend.application.port.`in`.ProductUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.UpdateProductCommand
import pl.szczygieldev.ecommercebackend.application.port.out.Products
import pl.szczygieldev.ecommercebackend.domain.*
import pl.szczygieldev.ecommercebackend.domain.error.AppError
import pl.szczygieldev.ecommercebackend.domain.error.ProductNotFoundError
import pl.szczygieldev.ecommercebackend.domain.event.ProductEvent
import pl.szczygieldev.shared.architecture.UseCase
import pl.szczygieldev.shared.ddd.core.DomainEventPublisher
import java.math.BigDecimal

@UseCase
class ProductService(val products: Products, val productEventPublisher: DomainEventPublisher<ProductEvent>) :
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