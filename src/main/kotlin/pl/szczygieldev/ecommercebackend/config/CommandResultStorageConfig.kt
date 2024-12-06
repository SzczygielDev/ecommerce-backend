package pl.szczygieldev.ecommercebackend.config

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