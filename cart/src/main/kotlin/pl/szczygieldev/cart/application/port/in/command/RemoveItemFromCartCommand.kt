package pl.szczygieldev.cart.application.port.`in`.command

import pl.szczygieldev.ecommercelibrary.command.Command
import pl.szczygieldev.cart.domain.AppError
import java.util.UUID

internal data class RemoveItemFromCartCommand(val cartId: UUID, val productId: UUID) : Command<AppError>()