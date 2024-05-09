package pl.szczygieldev.ecommercebackend.domain.event

import pl.szczygieldev.ddd.core.DomainEvent
import java.time.Instant

sealed class ProductEvent : DomainEvent

class ProductCreated : ProductEvent(){
    override val occurredOn: Instant = Instant.now()
}