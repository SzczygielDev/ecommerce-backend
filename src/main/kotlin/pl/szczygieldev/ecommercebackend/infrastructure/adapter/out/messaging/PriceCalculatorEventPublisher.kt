package pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.messaging

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import pl.szczygieldev.ddd.core.DomainEventPublisher
import pl.szczygieldev.ecommercebackend.domain.event.PriceCalculatorEvent

@Component
class PriceCalculatorEventPublisher(private val eventPublisher: ApplicationEventPublisher) :
    DomainEventPublisher<PriceCalculatorEvent> {
    companion object {
        private val log = KotlinLogging.logger { }
    }
    override fun publish(domainEvent: PriceCalculatorEvent) {
        log.info { "publishing event='$domainEvent'" }
        eventPublisher.publishEvent(domainEvent)
    }
}

