package pl.szczygieldev.product.infrastructure.adapter.out.outbox

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.szczygieldev.ecommercelibrary.outbox.InMemoryOutbox
import pl.szczygieldev.ecommercelibrary.outbox.Outbox
import pl.szczygieldev.ecommercelibrary.outbox.OutboxBackgroundWorker

@Configuration("productModule.OutboxConfig")
class OutboxConfig {

    @Bean("productModule.Outbox")
    fun outbox(objectMapper: ObjectMapper): Outbox{
        return InMemoryOutbox(objectMapper);
    }

    @Bean("productModule.OutboxBackgroundWorker")
    fun outboxWorker(outbox: Outbox, objectMapper: ObjectMapper): OutboxBackgroundWorker{
        return OutboxBackgroundWorker(outbox,objectMapper)
    }
}