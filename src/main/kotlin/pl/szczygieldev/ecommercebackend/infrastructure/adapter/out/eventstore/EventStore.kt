package pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.eventstore

import pl.szczygieldev.ddd.core.DomainEvent
import pl.szczygieldev.ddd.core.Identity

interface EventStore {
    fun appendEvents(aggregateId: Identity<*>, events: List<DomainEvent<*>>, exceptedVersion: Int)

    fun <T : DomainEvent<T>> getEvents(aggregateId: Identity<*>): List<T>?
}