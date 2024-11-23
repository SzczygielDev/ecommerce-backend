package pl.szczygieldev.order.infrastructure.adapter.`in`.command

import arrow.core.Either
import pl.szczygieldev.order.application.port.`in`.command.common.Command
import pl.szczygieldev.order.domain.error.AppError

interface Mediator {
    suspend fun send(command: Command): Either<AppError, Unit>

    suspend fun sendAsync(command: Command)
}