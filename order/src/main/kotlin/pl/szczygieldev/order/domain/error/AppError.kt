package pl.szczygieldev.order.domain.error

import pl.szczygieldev.ecommercelibrary.command.CommandError

sealed class AppError(override val message: String) : CommandError(message)
abstract class InfrastructureError(message: String) : AppError(message)