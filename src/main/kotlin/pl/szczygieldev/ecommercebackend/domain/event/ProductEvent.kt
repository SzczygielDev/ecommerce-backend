package pl.szczygieldev.ecommercebackend.domain.event

import DomainEvent
import java.time.Instant

sealed class ProductEvent : DomainEvent

class ProductCreated : ProductEvent(){
    override val occurredOn: Instant = Instant.now()
}