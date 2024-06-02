package pl.szczygieldev.shared.outbox

import pl.szczygieldev.shared.ddd.core.DomainEvent

interface Outbox {
    fun insertEvent(event: DomainEvent<*>)
    fun insertEvents(events: List<DomainEvent<*>>)
    fun markAsProcessed(event: DomainEvent<*>)
}