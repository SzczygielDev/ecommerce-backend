package pl.szczygieldev.order.application.port.`in`.command

import pl.szczygieldev.ecommercelibrary.command.Command
import pl.szczygieldev.order.domain.error.AppError

internal class CreateCartCommand : Command<AppError>()