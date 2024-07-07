package pl.szczygieldev.ecommercebackend.application.port.out

import arrow.core.Either
import pl.szczygieldev.ecommercebackend.domain.error.AppError
import pl.szczygieldev.shared.architecture.Command
import pl.szczygieldev.shared.architecture.CommandId
import pl.szczygieldev.shared.architecture.CommandResult

interface CommandStorage {
    fun runCommand(command: Command)
    fun commandFailed(id: CommandId, error:AppError): Either<AppError,Unit>
    fun commandFailed(id: CommandId, errors: List<AppError>): Either<AppError,Unit>
    fun commandSuccess(id: CommandId): Either<AppError,Unit>
    fun findById(id: CommandId): CommandResult?
}

