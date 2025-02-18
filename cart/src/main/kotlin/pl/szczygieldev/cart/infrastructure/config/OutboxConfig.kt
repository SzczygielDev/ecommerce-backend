package pl.szczygieldev.cart.infrastructure.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.szczygieldev.ecommercelibrary.outbox.InMemoryOutbox
import pl.szczygieldev.ecommercelibrary.outbox.Outbox
import pl.szczygieldev.ecommercelibrary.outbox.OutboxBackgroundWorker

@Configuration
internal class OutboxConfig(val eventPublisher : ApplicationEventPublisher) {

    @Bean
    fun outbox(objectMapper: ObjectMapper): Outbox {
        return InMemoryOutbox(objectMapper);
    }

    @Bean
    fun outboxWorker(outbox: Outbox, objectMapper: ObjectMapper): OutboxBackgroundWorker {
        return OutboxBackgroundWorker(outbox,objectMapper) {
            event -> eventPublisher.publishEvent(event)
        }
    }
}