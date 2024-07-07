package pl.szczygieldev.shared.architecture

import java.util.UUID

data class CommandId(val id: String = UUID.randomUUID().toString())