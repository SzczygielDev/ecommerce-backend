package pl.szczygieldev.order.infrastructure.adapter.out.command

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.szczygieldev.ecommercelibrary.command.CommandResultStorage
import pl.szczygieldev.ecommercelibrary.command.InMemoryCommandResultStorage

@Configuration
class CommandResultStorageConfig {

    @Bean
    fun commandResultStorage(): CommandResultStorage{
        return InMemoryCommandResultStorage()
    }
}