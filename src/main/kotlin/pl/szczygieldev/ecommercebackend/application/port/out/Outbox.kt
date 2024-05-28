package pl.szczygieldev.ecommercebackend.application.port.out

import pl.szczygieldev.ddd.core.DomainEvent

interface Outbox {
    fun insertEvent(event:DomainEvent<*>)
    fun insertEvents(events: List<DomainEvent<*>>)
    fun markAsProcessed(event:DomainEvent<*>)
}