package pl.szczygieldev.order.infrastructure.adapter.error

import pl.szczygieldev.order.application.port.`in`.command.common.CommandId
import pl.szczygieldev.order.domain.error.InfrastructureError

sealed class CommandStorageError(message: String) : InfrastructureError(message)

data class CommandNotFoundError(override val message: String) : CommandStorageError(message) {
    companion object {
        fun forId(id: CommandId): CommandNotFoundError {
            return CommandNotFoundError("Cannot find command with id='${id.id}'.")
        }
    }
}

data class CommandAlreadyProcessingError(override val message: String) : CommandStorageError(message) {
    companion object {
        fun forId(id: CommandId): CommandAlreadyProcessingError {
            return CommandAlreadyProcessingError("Command with id='${id.id}' is processing or already been processed.")
        }
    }
}