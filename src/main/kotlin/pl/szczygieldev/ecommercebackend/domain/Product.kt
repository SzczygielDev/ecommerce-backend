package pl.szczygieldev.ecommercebackend.domain

import pl.szczygieldev.ecommercebackend.domain.event.CartEvent
import pl.szczygieldev.ecommercebackend.domain.event.ProductCreated
import pl.szczygieldev.ecommercebackend.domain.event.ProductEvent

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
        ): Product = Product(productId, title, description, price).also {
            it.addEvent(ProductCreated())
        }
    }
}
