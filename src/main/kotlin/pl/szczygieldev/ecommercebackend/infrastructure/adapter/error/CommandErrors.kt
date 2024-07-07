package pl.szczygieldev.ecommercebackend.infrastructure.adapter.error

import pl.szczygieldev.ecommercebackend.domain.error.InfrastructureError
import pl.szczygieldev.shared.architecture.CommandId

sealed class CommandStorageError(message: String) : InfrastructureError(message)

data class CommandNotFoundError(override val message: String) : CommandStorageError(message) {
    companion object {
        fun forId(id: CommandId): CommandNotFoundError {
            return CommandNotFoundError("Cannot find command with id='${id.id}'.")
        }
    }
}