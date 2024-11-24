package pl.szczygieldev.ecommercebackend.config

import com.trendyol.kediatr.CommandWithResultHandler
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType
import pl.szczygieldev.shared.architecture.UseCase

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
            value = [UseCase::class]

        ),
        ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            value = [CommandWithResultHandler::class]
        )
    ]
)
class ScanConfig