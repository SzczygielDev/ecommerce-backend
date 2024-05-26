package pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.messaging

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import pl.szczygieldev.ddd.core.DomainEventPublisher
import pl.szczygieldev.ecommercebackend.domain.event.PriceCalculatorEvent

@Component
class PriceCalculatorEventPublisher(private val eventPublisher: ApplicationEventPublisher) :
    DomainEventPublisher<PriceCalculatorEvent> {
    override fun publish(domainEvent: PriceCalculatorEvent) {
        eventPublisher.publishEvent(domainEvent)
    }
}

