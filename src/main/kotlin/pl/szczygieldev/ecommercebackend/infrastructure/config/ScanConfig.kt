package pl.szczygieldev.ecommercebackend.infrastructure.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType
import pl.szczygieldev.ecommercebackend.application.architecture.UseCase

@Configuration
@ComponentScan(
    basePackages = ["pl.szczygieldev.ecommercebackend"],
    includeFilters = [
        ComponentScan.Filter(
            type = FilterType.ANNOTATION,
            value = [UseCase::class]
        )
    ]
)
class ScanConfig