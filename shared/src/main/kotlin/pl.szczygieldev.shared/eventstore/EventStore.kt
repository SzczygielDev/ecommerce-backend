package pl.szczygieldev.shared.eventstore

import pl.szczygieldev.shared.ddd.core.DomainEvent
import pl.szczygieldev.shared.ddd.core.Identity

interface EventStore {
    fun appendEvents(aggregateId: Identity<*>, events: List<DomainEvent<*>>, exceptedVersion: Int)

    fun <T : DomainEvent<T>> getEvents(aggregateId: Identity<*>): List<T>?
}