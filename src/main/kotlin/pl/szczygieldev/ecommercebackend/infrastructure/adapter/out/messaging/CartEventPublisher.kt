package pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.messaging

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import pl.szczygieldev.ddd.core.DomainEventPublisher
import pl.szczygieldev.ecommercebackend.domain.event.CartEvent

@Component
class CartEventPublisher(private val eventPublisher: ApplicationEventPublisher) : DomainEventPublisher<CartEvent> {
    companion object {
        private val log = KotlinLogging.logger { }
    }
    override fun publish(domainEvent: CartEvent) {
        log.info { "publishing event='$domainEvent'" }
        eventPublisher.publishEvent(domainEvent)
    }
}