package pl.szczygieldev.order.infrastructure.adapter.out.command

import arrow.core.Either
import pl.szczygieldev.order.application.port.`in`.command.common.Command
import pl.szczygieldev.order.application.port.`in`.command.common.CommandId
import pl.szczygieldev.order.domain.error.AppError
import pl.szczygieldev.order.infrastructure.adapter.`in`.command.common.CommandResult

interface CommandResultStorage {
    fun commandBegin(command: Command): Either<AppError,Unit>
    fun commandFailed(id: CommandId, error: AppError): Either<AppError,Unit>
    fun commandFailed(id: CommandId, errors: List<AppError>): Either<AppError,Unit>
    fun commandSuccess(id: CommandId): Either<AppError,Unit>
    fun findById(id: CommandId): CommandResult?
    fun findAll(): List<CommandResult>
}

