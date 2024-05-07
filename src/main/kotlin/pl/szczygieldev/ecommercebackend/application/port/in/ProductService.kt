package pl.szczygieldev.ecommercebackend.application.port.`in`

import pl.szczygieldev.ecommercebackend.application.port.`in`.command.CreateProductCommand
import pl.szczygieldev.ecommercebackend.application.port.out.Products
import pl.szczygieldev.ecommercebackend.domain.Product
import pl.szczygieldev.ecommercebackend.domain.ProductDescription
import pl.szczygieldev.ecommercebackend.domain.ProductPrice
import pl.szczygieldev.ecommercebackend.domain.ProductTitle
import java.math.BigDecimal

class ProductService(val products: Products) {
    fun create(command: CreateProductCommand) : Product{
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