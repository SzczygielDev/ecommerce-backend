package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.command.common

import pl.szczygieldev.ecommercebackend.application.port.`in`.command.common.Command
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.common.CommandId
import java.time.Duration
import java.time.Instant

class CommandResult(
    val id: CommandId,
    var command: Command,
    var status: CommandResultStatus,
    var timestamp: Instant,
    var duration: Duration,
    var errors: MutableList<CommandResultError>
)