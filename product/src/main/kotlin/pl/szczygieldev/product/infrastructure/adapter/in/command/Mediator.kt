package pl.szczygieldev.product.infrastructure.adapter.`in`.command

import arrow.core.Either
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.common.Command
import pl.szczygieldev.product.domain.error.AppError

interface Mediator {
    suspend fun send(command: Command): Either<AppError, Unit>

    suspend fun sendAsync(command: Command)
}