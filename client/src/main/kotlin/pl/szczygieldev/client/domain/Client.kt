package pl.szczygieldev.client.domain

class Client(
    val id: ClientId,
    val name: String,
    val lastName: String,
    val email: ClientEmail,
    val phone: ClientPhoneNumber,
    val accountType: AccountType,
    val address: ClientAddress
)