package pl.szczygieldev.ecommercebackend.domain.event
import pl.szczygieldev.ddd.core.DomainEvent
import java.time.Instant

sealed class CartEvent : DomainEvent

class ItemAddedToCart : CartEvent(){
    override val occurredOn: Instant = Instant.now()
}

class ItemRemovedFromCart : CartEvent(){
    override val occurredOn: Instant = Instant.now()
}

class CartSubmitted : CartEvent(){
    override val occurredOn: Instant = Instant.now()
}