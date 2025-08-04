package pl.szczygieldev.client.infrastructure.adapter.`in`.http.resource

data class ClientUpdateRequest(
    val name: String,
    val lastName: String,
    val phone: String,
    val city: String,
    val zipCode: String,
    val street: String,
    val houseNumber: String
)
