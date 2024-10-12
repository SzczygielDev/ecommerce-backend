package pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.outbox

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import pl.szczygieldev.shared.ddd.core.DomainEvent
import pl.szczygieldev.shared.outbox.Outbox

@Component
class OutboxBackgroundWorker(
    val outbox: Outbox,
    val objectMapper: ObjectMapper
) {
    companion object {
        private val log = KotlinLogging.logger { }
    }

    @Scheduled(fixedRate = 5000)
    fun processOutbox() {
        outbox.getEventsForProcessing().map { outboxMessage ->
            objectMapper.readerFor(Class.forName(outboxMessage.eventType))
                .readValue<DomainEvent<*>>(outboxMessage.eventData)
        }
            .forEach { event ->
                log.info { "publishing event='$event'" }
                // map to integration event and call message broker
                outbox.markAsProcessed(event)
            }
    }
}