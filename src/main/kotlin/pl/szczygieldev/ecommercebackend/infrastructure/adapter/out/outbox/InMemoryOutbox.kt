package pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.outbox

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import pl.szczygieldev.shared.ddd.core.DomainEvent
import pl.szczygieldev.shared.outbox.Outbox
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.outbox.model.OutboxMessage
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.outbox.model.OutboxMessageStatus

@Component
class InMemoryOutbox(val objectMapper: ObjectMapper) : Outbox {
    private val db = mutableSetOf<OutboxMessage>()
    override fun insertEvent(event: DomainEvent<*>) {
        db.add(
            OutboxMessage(
                event.id,
                OutboxMessageStatus.PENDING,
                objectMapper.writeValueAsString(event),
                event.javaClass.typeName,
                event.occurredOn
            )
        )
    }

    override fun insertEvents(events: List<DomainEvent<*>>) {
        events.forEach {
            event -> insertEvent(event)
        }
    }

    override fun markAsProcessed(event: DomainEvent<*>) {
        db.find { message -> message.eventId == event.id }?.status = OutboxMessageStatus.PROCESSED
    }
}