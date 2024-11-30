package pl.szczygieldev.order.infrastructure.adapter.out.eventstore

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.szczygieldev.ecommercelibrary.eventstore.EventStore
import pl.szczygieldev.ecommercelibrary.eventstore.InMemoryEventStore

@Configuration
class EventStoreConfig {

    @Bean
    fun eventStore(objectMapper: ObjectMapper): EventStore{
        return InMemoryEventStore(objectMapper)
    }
}