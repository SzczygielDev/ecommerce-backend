package pl.szczygieldev.order.infrastructure.adapter.`in`.api.resource

import pl.szczygieldev.ecommercelibrary.command.CommandResultStatus
import java.time.Duration
import java.time.Instant

internal data class CommandResultFullDto(
    val id: String,
    val payload : Any,
    val status: CommandResultStatus,
    var timestamp: Instant,
    var duration: Duration,
    val errors: List<Error>
) {
    data class Error(val name: String, val message: String)
}