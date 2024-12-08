package pl.szczygieldev.order.infrastructure.adapter.error

import pl.szczygieldev.ecommercelibrary.command.CommandId
import pl.szczygieldev.order.domain.error.InfrastructureError

data class CommandNotFoundError(override val message: String) : InfrastructureError(message) {
    companion object {
        fun forId(id: CommandId): CommandNotFoundError {
            return CommandNotFoundError("Cannot find command with id='${id.id}'.")
        }
    }
}
