package pl.szczygieldev.ecommercebackend.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.szczygieldev.ecommercelibrary.eventstore.EventStore
import pl.szczygieldev.ecommercelibrary.eventstore.ExposedEventStore
import javax.sql.DataSource

@Configuration
class EventStoreConfig {

    @Bean
    fun eventStore(dataSource: DataSource, objectMapper: ObjectMapper): EventStore {
        return ExposedEventStore(dataSource, objectMapper)
    }
}