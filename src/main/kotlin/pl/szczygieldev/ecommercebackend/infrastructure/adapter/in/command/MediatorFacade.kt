package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.command

import arrow.core.Either
import com.trendyol.kediatr.Mediator
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.common.Command
import pl.szczygieldev.ecommercebackend.application.port.out.CommandResultStorage
import pl.szczygieldev.ecommercebackend.domain.error.AppError

@Component
class MediatorFacade(val kediatr: Mediator, val commandResultStorage: CommandResultStorage) {
    private val log = KotlinLogging.logger(javaClass.name)
    private val coroutineScope =
        CoroutineScope(Job() + CoroutineExceptionHandler { context, throwable -> log.error { "Exception while processing handler in background: $throwable" } })

    suspend fun send(command: Command): Either<AppError, Unit> {
        commandResultStorage.commandBegin(command)

        val result = kediatr.send(command)

        result.fold({
            handleFailure(command, it)
        }, {
            handleSuccess(command)
        })

        return result
    }

    suspend fun sendAsync(command: Command) {
        commandResultStorage.commandBegin(command)

        coroutineScope.launch {
            val result = kediatr.send(command)

            result.fold({
                handleFailure(command, it)
            }, {
                handleSuccess(command)
            })
        }
    }


    private fun handleSuccess(command: Command) {
        log.info { "Command with id='${command.id}' finished successfully" }
        commandResultStorage.commandSuccess(command.id)
    }

    private fun handleFailure(command: Command, error: AppError) {
        log.error { "Error while processing command with id='${command.id}' error='$error'" }
        commandResultStorage.commandFailed(command.id, error)
    }
}