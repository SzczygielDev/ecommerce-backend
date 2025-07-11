package pl.szczygieldev.client.domain

import java.util.UUID

class Client(
    val id: ClientId,
    val externalId: UUID,
    val name: String,
    val lastName: String,
    val email: ClientEmail,
    val phone: ClientPhoneNumber,
    val accountType: AccountType,
    val address: ClientAddress
)