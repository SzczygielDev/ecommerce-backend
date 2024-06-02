package pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.eventstore

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import pl.szczygieldev.ddd.core.DomainEvent
import pl.szczygieldev.ddd.core.Identity
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.eventstore.exception.EventStoreLockingException
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.eventstore.model.Stream
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.eventstore.model.StreamEntry
import java.lang.RuntimeException

@Component
class InMemoryEventStore(val objectMapper: ObjectMapper) : EventStore {

    private val store = mutableMapOf<String, Stream>()
    override fun appendEvents(aggregateId: Identity<*>, events: List<DomainEvent<*>>, exceptedVersion: Int) {
        val foundStream = store[aggregateId.id()]

        var eventsForAggregate = foundStream?.getSortedEvents()?.toMutableList()

        var currentVersion = eventsForAggregate?.lastOrNull()?.version

        if (eventsForAggregate == null) {
            currentVersion = 0
            eventsForAggregate = mutableListOf()
        } else if (currentVersion != exceptedVersion) {
            throw EventStoreLockingException(aggregateId,currentVersion,exceptedVersion)
        }

        var versionOfCurrentEvent = currentVersion
        events.forEach { event ->
            versionOfCurrentEvent++

            eventsForAggregate.add(
                StreamEntry(
                    aggregateId,
                    versionOfCurrentEvent,
                    event.javaClass.typeName,
                    objectMapper.writeValueAsString(event)
                )
            )
        }

        store[aggregateId.id()] = Stream.of(aggregateId, eventsForAggregate)
    }

    final override fun <T : DomainEvent<T>> getEvents(aggregateId: Identity<*>): List<T>? {
        return store[aggregateId.id()]?.getSortedEvents()?.map {
            return@map objectMapper.readerFor(Class.forName(it.eventType)).readValue<T>(it.eventData)
        }
    }
}