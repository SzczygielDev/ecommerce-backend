package pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.messaging.publisher

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.ApplicationEventPublisher
import pl.szczygieldev.shared.ddd.core.DomainEvent
import pl.szczygieldev.shared.ddd.core.DomainEventPublisher
import pl.szczygieldev.shared.outbox.Outbox

abstract class StoreAndForwardEventPublisher<T : DomainEvent<T>>(
    val eventPublisher: ApplicationEventPublisher,
    val outbox: Outbox
) : DomainEventPublisher<T> {
    companion object {
        private val log = KotlinLogging.logger { }
    }

    override fun publish(domainEvent: T) {
        outbox.insertEvent(domainEvent)
        processOutbox(listOf(domainEvent))
    }

    override fun publishBatch(events: List<T>) {
        outbox.insertEvents(events)
        processOutbox(events)
    }

    /*
    * Due to learning purposes for now executing outbox processing is done on every publish call, in future will be from background worker.
    */
    private fun processOutbox(events: List<T>) {
        events.forEach { event ->
            log.info { "publishing event='$event'" }
            eventPublisher.publishEvent(event)
            outbox.markAsProcessed(event)
        }
    }
}