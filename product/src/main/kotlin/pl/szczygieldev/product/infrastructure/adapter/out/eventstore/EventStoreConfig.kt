package pl.szczygieldev.product.infrastructure.adapter.out.eventstore

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.szczygieldev.ecommercelibrary.eventstore.EventStore
import pl.szczygieldev.ecommercelibrary.eventstore.InMemoryEventStore

@Configuration("productModule.EventStoreConfig")
class EventStoreConfig {

    @Bean("productModule.EventStore")
    fun eventStore(objectMapper: ObjectMapper): EventStore{
        return InMemoryEventStore(objectMapper)
    }
}