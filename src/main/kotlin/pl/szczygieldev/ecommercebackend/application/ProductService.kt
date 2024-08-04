package pl.szczygieldev.ecommercebackend.application

import arrow.core.Either
import arrow.core.raise.either
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.CreateProductCommand
import pl.szczygieldev.ecommercebackend.application.port.`in`.ProductUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.UpdateProductCommand
import pl.szczygieldev.ecommercebackend.application.port.out.Products
import pl.szczygieldev.ecommercebackend.domain.Product
import pl.szczygieldev.ecommercebackend.domain.ProductDescription
import pl.szczygieldev.ecommercebackend.domain.ProductPrice
import pl.szczygieldev.ecommercebackend.domain.ProductTitle
import pl.szczygieldev.ecommercebackend.domain.error.AppError
import pl.szczygieldev.ecommercebackend.domain.error.ProductNotFoundError
import pl.szczygieldev.shared.architecture.UseCase
import java.math.BigDecimal

@UseCase
class ProductService(val products: Products) : ProductUseCase {
    override fun create(command: CreateProductCommand): Product {
        val product = Product.create(
            products.nextIdentity(),
            ProductTitle(command.title),
            ProductDescription(command.description),
            ProductPrice(BigDecimal.valueOf(command.price))
        )

        products.save(product)
        return product
    }

    override fun update(command: UpdateProductCommand): Either<AppError, Product> = either {
        val productId = command.productId
        val product = products.findById(productId) ?: raise(ProductNotFoundError(productId.id))

        product.title = command.title
        product.description = command.description
        product.price = command.price

        products.save(product)
    }
}