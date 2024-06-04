package pl.szczygieldev.ecommercebackend.domain

import pl.szczygieldev.ecommercebackend.domain.event.ProductCreated
import pl.szczygieldev.ecommercebackend.domain.event.ProductEvent
import java.math.BigDecimal

class Product private constructor(
    val productId: ProductId,
    var title: ProductTitle,
    var description: ProductDescription,
    var price: ProductPrice
) {
    private val events = mutableListOf<ProductEvent>()

    private fun addEvent(event: ProductEvent) {
        events.add(event)
    }

    companion object {
        fun create(
            productId: ProductId,
            title: ProductTitle,
            description: ProductDescription,
            price: ProductPrice
        ): Product {
            require(price.amount > BigDecimal.ZERO) { "Product price must be positive value, provided='$price'" }
            return Product(productId, title, description, price).also {
                it.addEvent(ProductCreated())
            }
        }
    }
}
