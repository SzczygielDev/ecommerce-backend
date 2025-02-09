package pl.szczygieldev.ecommercebackend.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.szczygieldev.ecommercelibrary.command.Command
import pl.szczygieldev.ecommercelibrary.command.CommandProcessor
import pl.szczygieldev.ecommercelibrary.command.CommandQueue
import pl.szczygieldev.ecommercelibrary.command.Mediator
import pl.szczygieldev.ecommercelibrary.messaging.InMemoryMessageQueue
import pl.szczygieldev.ecommercelibrary.messaging.config.MessageQueueConfig

@Configuration
class CommandProcessingConfig {
    @Bean
    fun commandQueue(mediator: Mediator): CommandQueue {
        val queue = InMemoryMessageQueue<Command<*>>(MessageQueueConfig())

        // Command processor pool should be configurable
        for (i in 1..100) {
            queue.registerListener(CommandProcessor(mediator, queue))
        }

        return CommandQueue(queue)
    }
}