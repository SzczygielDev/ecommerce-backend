package pl.szczygieldev.product.infrastructure.adapter.out.command

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.szczygieldev.ecommercelibrary.command.CommandResultStorage
import pl.szczygieldev.ecommercelibrary.command.InMemoryCommandResultStorage

@Configuration
class CommandResultStorageConfig {

    @Bean("product.InMemoryCommandResultStorage")
    fun commandResultStorage(): CommandResultStorage{
        return InMemoryCommandResultStorage()
    }
}