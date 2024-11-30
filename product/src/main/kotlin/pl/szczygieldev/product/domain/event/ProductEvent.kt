package pl.szczygieldev.product.domain.event

import pl.szczygieldev.ecommercelibrary.ddd.core.DomainEvent
import pl.szczygieldev.product.domain.*

sealed class ProductEvent : DomainEvent<ProductEvent>()

class ProductCreated(val productId: ProductId, val title: ProductTitle, val description: ProductDescription, val price: ProductPrice, val imageId: ImageId) : ProductEvent(){
    override fun toString(): String {
        return "ProductCreated(id=$id occuredOn=$occurredOn title=$title description=$description price=$price imageId=$imageId)"
    }
}

class ProductPriceUpdated(val productId: ProductId, val newPrice: ProductPrice) : ProductEvent(){
    override fun toString(): String {
        return "ProductPriceUpdated(id=$id occuredOn=$occurredOn newPrice=$newPrice)"
    }
}