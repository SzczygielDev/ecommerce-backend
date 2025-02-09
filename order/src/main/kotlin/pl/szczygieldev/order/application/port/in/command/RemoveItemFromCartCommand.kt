package pl.szczygieldev.order.application.port.`in`.command

import pl.szczygieldev.ecommercelibrary.command.Command
import pl.szczygieldev.order.domain.error.AppError
import java.util.UUID

internal data class RemoveItemFromCartCommand(val cartId: UUID, val productId: UUID) : Command<AppError>()