package pl.szczygieldev.client.infrastructure.adapter.`in`.http.presenter

import org.springframework.stereotype.Component
import pl.szczygieldev.client.domain.Client
import pl.szczygieldev.client.infrastructure.adapter.`in`.http.resource.ClientDto

@Component
class ClientPresenter {
    fun toDto(client: Client): ClientDto {
        return ClientDto(
            client.id.idAsUUID(),
            client.name,
            client.lastName,
            client.email.value,
            client.phone.value,
            client.accountType,
            client.address.city,
            client.address.zipCode,
            client.address.street,
            client.address.houseNumber
        )
    }
}