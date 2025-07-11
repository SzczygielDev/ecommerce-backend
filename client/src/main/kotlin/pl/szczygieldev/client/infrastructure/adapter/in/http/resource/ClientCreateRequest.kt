package pl.szczygieldev.client.infrastructure.adapter.`in`.http.resource

data class ClientCreateRequest(
    val name: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val city: String,
    val zipCode: String,
    val street: String,
    val houseNumber: String
)