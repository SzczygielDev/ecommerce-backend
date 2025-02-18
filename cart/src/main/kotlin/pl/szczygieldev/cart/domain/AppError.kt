package pl.szczygieldev.cart.domain

import pl.szczygieldev.ecommercelibrary.command.CommandError

internal sealed class AppError(override val message: String) : CommandError(message,"") // FIXME - add error codes support
internal abstract class InfrastructureError(message: String) : AppError(message)