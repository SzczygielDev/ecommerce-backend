package pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.command

import arrow.core.Either
import arrow.core.raise.either
import org.springframework.stereotype.Repository
import pl.szczygieldev.ecommercebackend.application.handlers.common.*
import pl.szczygieldev.ecommercebackend.application.port.out.CommandResultStorage
import pl.szczygieldev.ecommercebackend.domain.error.AppError
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.error.CommandAlreadyProcessingError
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.error.CommandNotFoundError
import java.time.Duration
import java.time.Instant

@Repository
class InMemoryCommandResultStorage : CommandResultStorage {
    private val db = mutableMapOf<CommandId, CommandResult>()

    override fun findById(id: CommandId): CommandResult? = db[id]
    override fun findAll(): List<CommandResult> = db.values.toList()

    override fun commandBegin(command: Command): Either<AppError, Unit>  = either {
        if(db.containsKey(command.id)){
            raise(CommandAlreadyProcessingError.forId(command.id))
        }

        db.put(
            command.id,
            CommandResult(command.id, CommandResultStatus.RUNNING, Instant.now(), Duration.ZERO, mutableListOf())
        )
    }

    override fun commandFailed(id: CommandId, error: AppError): Either<AppError,Unit> = either {
        val foundCommand = findById(id) ?: raise(CommandNotFoundError.forId(id))

        foundCommand.status = CommandResultStatus.ERROR
        foundCommand.duration = Duration.between(foundCommand.timestamp, Instant.now())
        foundCommand.errors.add(CommandResultError(error.javaClass.name, error.message))
    }

    override fun commandFailed(id: CommandId, errors: List<AppError>) : Either<AppError,Unit> = either{
        val foundCommand = findById(id) ?: raise(CommandNotFoundError.forId(id))

        foundCommand.status = CommandResultStatus.ERROR
        foundCommand.duration = Duration.between(foundCommand.timestamp, Instant.now())
        foundCommand.errors.addAll(errors.map { error -> CommandResultError(error.javaClass.name, error.message) })
    }

    override fun commandSuccess(id: CommandId) : Either<AppError,Unit> = either{
        val foundCommand = findById(id) ?: raise(CommandNotFoundError.forId(id))

        foundCommand.status = CommandResultStatus.SUCCESS
        foundCommand.duration = Duration.between(foundCommand.timestamp, Instant.now())
    }
}