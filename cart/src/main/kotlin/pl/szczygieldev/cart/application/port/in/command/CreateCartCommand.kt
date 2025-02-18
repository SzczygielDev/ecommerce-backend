package pl.szczygieldev.cart.application.port.`in`.command

import pl.szczygieldev.ecommercelibrary.command.Command
import pl.szczygieldev.cart.domain.AppError

internal class CreateCartCommand : Command<AppError>()