package pl.szczygieldev.ecommercebackend.application.port.out

import arrow.core.Either
import pl.szczygieldev.ecommercebackend.domain.error.AppError
import pl.szczygieldev.ecommercebackend.application.handlers.common.Command
import pl.szczygieldev.ecommercebackend.application.handlers.common.CommandId
import pl.szczygieldev.ecommercebackend.application.handlers.common.CommandResult

interface CommandResultStorage {
    fun commandBegin(command: Command): Either<AppError,Unit>
    fun commandFailed(id: CommandId, error:AppError): Either<AppError,Unit>
    fun commandFailed(id: CommandId, errors: List<AppError>): Either<AppError,Unit>
    fun commandSuccess(id: CommandId): Either<AppError,Unit>
    fun findById(id: CommandId): CommandResult?
    fun findAll(): List<CommandResult>
}

