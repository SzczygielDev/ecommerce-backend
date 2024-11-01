package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.presenter

import org.springframework.stereotype.Component
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.resource.CommandResultDto
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.command.common.CommandResult
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.resource.CommandResultFullDto

@Component
class CommandPresenter {
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