package pl.szczygieldev.ecommercebackend.infrastructure.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType
import pl.szczygieldev.shared.architecture.UseCase
import pl.szczygieldev.shared.architecture.CommandHandler

@Configuration
@ComponentScan(
    basePackages = ["pl.szczygieldev.ecommercebackend"],
    includeFilters = [
        ComponentScan.Filter(
            type = FilterType.ANNOTATION,
            value = [UseCase::class]

        ),
        ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            value = [CommandHandler::class]
        )
    ]
)
class ScanConfig