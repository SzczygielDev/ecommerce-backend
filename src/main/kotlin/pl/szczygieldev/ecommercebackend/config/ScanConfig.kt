package pl.szczygieldev.ecommercebackend.config

import com.trendyol.kediatr.CommandWithResultHandler
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType
import pl.szczygieldev.ecommercelibrary.architecture.UseCase
import pl.szczygieldev.ecommercelibrary.ddd.core.DomainService

@Configuration
@ComponentScan(
    basePackages = ["pl.szczygieldev.ecommercebackend",
                    "pl.szczygieldev.product",
                    "pl.szczygieldev.order",
                    "pl.szczygieldev.external"
                   ],
    includeFilters = [
        ComponentScan.Filter(
            type = FilterType.ANNOTATION,
            value = [UseCase::class, DomainService::class]

        ),
        ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            value = [CommandWithResultHandler::class]
        )
    ]
)
class ScanConfig