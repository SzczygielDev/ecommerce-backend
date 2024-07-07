package pl.szczygieldev.shared.architecture

import java.time.Duration
import java.time.Instant

// TODO - should contain type of command and payload
class CommandResult(
    val id: CommandId,
    var status: CommandResultStatus,
    var timestamp: Instant,
    var duration: Duration,
    var errors: MutableList<CommandResultError>
)