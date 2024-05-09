package pl.szczygieldev.ecommercebackend.application

import pl.szczygieldev.ecommercebackend.application.architecture.UseCase
import pl.szczygieldev.ecommercebackend.application.command.CreateProductCommand
import pl.szczygieldev.ecommercebackend.application.port.`in`.ProductUseCase
import pl.szczygieldev.ecommercebackend.application.port.out.Products
import pl.szczygieldev.ecommercebackend.domain.Product
import pl.szczygieldev.ecommercebackend.domain.ProductDescription
import pl.szczygieldev.ecommercebackend.domain.ProductPrice
import pl.szczygieldev.ecommercebackend.domain.ProductTitle
import java.math.BigDecimal

@UseCase
private class ProductService(val products: Products) : ProductUseCase {
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
}