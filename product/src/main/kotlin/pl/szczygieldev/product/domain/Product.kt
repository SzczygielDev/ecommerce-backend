package pl.szczygieldev.product.domain

import pl.szczygieldev.ecommercelibrary.ddd.core.EventSourcedEntity
import pl.szczygieldev.product.domain.event.ProductCreated
import pl.szczygieldev.product.domain.event.ProductEvent
import pl.szczygieldev.product.domain.event.ProductPriceUpdated
import java.math.BigDecimal

internal class Product private constructor(
    val productId: ProductId,
    var title: ProductTitle,
    var description: ProductDescription,
    val basePrice: ProductPrice,
    var imageId: ImageId
) : EventSourcedEntity<ProductEvent>() {
    private var _price: ProductPrice = basePrice
    val price: ProductPrice get() = _price

    private var _priceChanges = mutableListOf<ProductPriceChange>()
    val priceChanges get() = _priceChanges.toList()

    companion object {
        fun create(
            productId: ProductId,
            title: ProductTitle,
            description: ProductDescription,
            price: ProductPrice,
            imageId: ImageId
        ): Product {
            require(price.amount > BigDecimal.ZERO) { "Product price must be positive value, provided='$price'" }
            val product = Product(productId, title, description, price,imageId)
            product.raiseEvent(ProductCreated(productId, title, description, price,imageId))
            return product
        }

        fun fromSnapshot(
            productId: ProductId,
            title: ProductTitle,
            description: ProductDescription,
            price: ProductPrice,
            imageId: ImageId
        ): Product {
            return Product(productId, title, description, price,imageId)
        }
    }

    fun applyEvents(events: List<ProductEvent>) {
        applyAll(events)
        clearOccurredEvents()
    }

    fun updatePrice(newPrice: ProductPrice) {
        if (newPrice != price) {
            raiseEvent(ProductPriceUpdated(productId, newPrice))
        }
    }

    override fun applyEvent(event: ProductEvent) {
        when (event) {
            is ProductCreated -> {}
            is ProductPriceUpdated -> apply(event)
        }
    }

    private fun apply(event: ProductPriceUpdated) {
        _price = event.newPrice
        _priceChanges.add(ProductPriceChange(event.newPrice, event.occurredOn))
    }
}
