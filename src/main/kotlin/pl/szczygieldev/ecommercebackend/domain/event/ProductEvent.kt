package pl.szczygieldev.ecommercebackend.domain.event

import pl.szczygieldev.shared.ddd.core.DomainEvent

sealed class ProductEvent : DomainEvent<ProductEvent>()

class ProductCreated : ProductEvent(){
    override fun toString(): String {
        return "ProductCreated(id=$id occuredOn=$occurredOn)"
    }
}