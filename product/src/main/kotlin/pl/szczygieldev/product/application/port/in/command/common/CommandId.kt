package pl.szczygieldev.ecommercebackend.application.port.`in`.command.common

import java.util.UUID

data class CommandId(val id: String = UUID.randomUUID().toString())