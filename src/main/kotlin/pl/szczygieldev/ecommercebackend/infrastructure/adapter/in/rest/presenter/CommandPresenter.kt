package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.presenter

import org.springframework.stereotype.Component
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.resource.CommandResultDto
import pl.szczygieldev.ecommercebackend.application.handlers.common.CommandResult

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
}