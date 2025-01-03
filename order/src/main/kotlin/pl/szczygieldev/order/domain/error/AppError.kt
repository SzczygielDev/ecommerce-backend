package pl.szczygieldev.order.domain.error

import pl.szczygieldev.ecommercelibrary.command.CommandError

internal sealed class AppError(override val message: String) : CommandError(message)
internal abstract class InfrastructureError(message: String) : AppError(message)