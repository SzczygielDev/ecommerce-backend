package pl.szczygieldev.ecommercebackend.application.handlers.common

import java.util.UUID

data class CommandId(val id: String = UUID.randomUUID().toString())