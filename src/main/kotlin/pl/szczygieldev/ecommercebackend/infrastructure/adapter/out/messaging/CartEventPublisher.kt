package pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.messaging

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import pl.szczygieldev.ddd.core.DomainEventPublisher
import pl.szczygieldev.ecommercebackend.domain.event.CartEvent

@Component
class CartEventPublisher(private val eventPublisher: ApplicationEventPublisher) : DomainEventPublisher<CartEvent> {
    override fun publish(domainEvent: CartEvent) {
        eventPublisher.publishEvent(domainEvent)
    }
}