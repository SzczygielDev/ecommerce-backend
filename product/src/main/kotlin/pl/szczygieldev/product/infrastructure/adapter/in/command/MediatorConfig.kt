package pl.szczygieldev.product.infrastructure.adapter.`in`.command

import com.trendyol.kediatr.PipelineBehavior
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.szczygieldev.ecommercelibrary.command.CommandResultStorage
import pl.szczygieldev.ecommercelibrary.command.ExposedTxPipelineBehaviour
import pl.szczygieldev.ecommercelibrary.command.Mediator
import pl.szczygieldev.ecommercelibrary.command.MediatorFacade

@Configuration
class MediatorConfig {
    @Bean("productModule.MediatorFacade")
    fun mediator(mediator: com.trendyol.kediatr.Mediator, commandResultStorage: CommandResultStorage): Mediator{
        return MediatorFacade(mediator,commandResultStorage)
    }

    @Bean("productModule.ExposedTxPipelineBehaviour")
    fun txPipelineBehaviour(): PipelineBehavior{
        return ExposedTxPipelineBehaviour()
    }
}