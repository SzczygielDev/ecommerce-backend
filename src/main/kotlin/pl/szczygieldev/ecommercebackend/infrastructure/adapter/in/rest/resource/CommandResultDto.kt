package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.resource

import pl.szczygieldev.ecommercebackend.application.handlers.common.CommandResultStatus
import java.time.Duration
import java.time.Instant

data class CommandResultDto(
    val id: String,
    val status: CommandResultStatus,
    var timestamp: Instant,
    var duration: Duration,
    val errors: List<Error>
) {
    data class Error(val name: String, val message: String)
}