package pl.szczygieldev.client.infrastructure.adapter.`in`.http.resource

import pl.szczygieldev.client.domain.AccountType
import java.util.*

data class ClientDto(
    val id: UUID,
    val name: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val accountType: AccountType,
    val city: String,
    val zipCode: String,
    val street: String,
    val houseNumber: String
)