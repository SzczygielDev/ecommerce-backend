package pl.szczygieldev.ecommercebackend.domain.event

import pl.szczygieldev.ecommercebackend.domain.ProductDescription
import pl.szczygieldev.ecommercebackend.domain.ProductId
import pl.szczygieldev.ecommercebackend.domain.ProductPrice
import pl.szczygieldev.ecommercebackend.domain.ProductTitle
import pl.szczygieldev.shared.ddd.core.DomainEvent

sealed class ProductEvent : DomainEvent<ProductEvent>()

class ProductCreated(val productId: ProductId, val title:ProductTitle, val description: ProductDescription, val price: ProductPrice) : ProductEvent(){
    override fun toString(): String {
        return "ProductCreated(id=$id occuredOn=$occurredOn title=$title description=$description price=$price)"
    }
}

class ProductPriceUpdated(val productId: ProductId, val newPrice: ProductPrice) : ProductEvent(){
    override fun toString(): String {
        return "ProductPriceUpdated(id=$id occuredOn=$occurredOn newPrice=$newPrice)"
    }
}