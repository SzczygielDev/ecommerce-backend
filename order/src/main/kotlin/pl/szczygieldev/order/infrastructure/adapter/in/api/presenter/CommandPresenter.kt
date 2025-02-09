package pl.szczygieldev.order.infrastructure.adapter.`in`.api.presenter

import org.springframework.stereotype.Component
import pl.szczygieldev.ecommercelibrary.command.CommandResult
import pl.szczygieldev.order.infrastructure.adapter.`in`.api.resource.CommandResultDto
import pl.szczygieldev.order.infrastructure.adapter.`in`.api.resource.CommandResultFullDto

@Component
internal class CommandPresenter {
    fun toDto(commandResult: CommandResult): CommandResultDto {
        return CommandResultDto(
            commandResult.id.id,
            commandResult.status,
            commandResult.timestamp,
            commandResult.duration,
            commandResult.errors.map { error -> CommandResultDto.Error(error.name, error.message) })
    }

    fun toFullDto(commandResult: CommandResult): CommandResultFullDto {
        return CommandResultFullDto(
            commandResult.id.id,
            commandResult.command,
            commandResult.status,
            commandResult.timestamp,
            commandResult.duration,
            commandResult.errors.map { error -> CommandResultFullDto.Error(error.name, error.message) })
    }
}