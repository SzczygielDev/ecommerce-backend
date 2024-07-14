package pl.szczygieldev.ecommercebackend.application.handlers.common

import arrow.core.Either
import arrow.core.raise.either
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import pl.szczygieldev.ecommercebackend.application.port.out.CommandResultStorage
import pl.szczygieldev.ecommercebackend.domain.error.AppError

abstract class CommandHandler<T : Command>(val commandResultStorage: CommandResultStorage) {
    private val log = KotlinLogging.logger(javaClass.name)

    private val coroutineScope =
        CoroutineScope(Job() + CoroutineExceptionHandler { context, throwable -> log.error { "Exception while processing handler in background: $throwable" } })

    suspend fun executeInBackground(command: T): Either<AppError, Unit> = either {
        log.info { "Executing command in background='$command'" }
        commandResultStorage.commandBegin(command).bind()

        coroutineScope.launch {
            processCommand(command).fold({
                handleFailure(command, it)
            }, {
                handleSuccess(command)
            })
        }
    }

    suspend fun execute(command: T): Either<AppError, Unit> = either {
        log.info { "Executing command='$command'" }
        commandResultStorage.commandBegin(command).bind()

        val result = either {
            processCommand(command).bind()
        }

        result.fold({
            handleFailure(command, it)
        }, {
            handleSuccess(command)
        })

        return result
    }

    private fun handleSuccess(command: T) {
        log.info { "Command with id='${command.id.id}' finished successfully" }
        commandResultStorage.commandSuccess(command.id)
    }

    private fun handleFailure(command: T, error: AppError) {
        log.error { "Error while processing command with id='${command.id.id}' error='$error'" }
        commandResultStorage.commandFailed(command.id, error)
    }

    protected abstract suspend fun processCommand(command: T): Either<AppError, Unit>
}
