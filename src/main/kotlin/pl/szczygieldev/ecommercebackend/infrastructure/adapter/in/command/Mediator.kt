package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.command

import arrow.core.Either
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.common.Command
import pl.szczygieldev.ecommercebackend.domain.error.AppError

interface Mediator {
    suspend fun send(command: Command): Either<AppError, Unit>

    suspend fun sendAsync(command: Command)
}