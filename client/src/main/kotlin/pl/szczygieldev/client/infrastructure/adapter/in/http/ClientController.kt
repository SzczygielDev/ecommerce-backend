package pl.szczygieldev.client.infrastructure.adapter.`in`.http

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.szczygieldev.client.domain.*
import pl.szczygieldev.client.infrastructure.adapter.`in`.http.presenter.ClientPresenter
import pl.szczygieldev.client.infrastructure.adapter.`in`.http.resource.ClientCreateRequest
import pl.szczygieldev.client.infrastructure.adapter.out.persistence.ClientRepository
import java.net.URI
import java.security.Principal
import java.util.*

@RequestMapping("/clients")
@RestController
class ClientController(val clientRepository: ClientRepository, val clientPresenter: ClientPresenter) {

    @GetMapping()
    fun getCurrentClient(principal: Principal): ResponseEntity<*> {
        val client =
            clientRepository.findByExternalId(UUID.fromString(principal.name)) ?: return ResponseEntity.notFound()
                .build<Any>()

        return ResponseEntity.ok().body(clientPresenter.toDto(client))
    }

    @PostMapping
    fun createClient(
        @RequestBody request: ClientCreateRequest,
        principal: Principal,
        servletRequest: HttpServletRequest
    ): ResponseEntity<*> {
        val client = Client(
            ClientId(UUID.randomUUID()),
            UUID.fromString(principal.name),
            request.name, request.lastName,
            ClientEmail(request.email),
            ClientPhoneNumber(request.phone),
            AccountType.STANDARD,
            ClientAddress(request.city, request.zipCode, request.street, request.houseNumber)
        )

        clientRepository.save(client)

        return ResponseEntity.created(URI.create("${servletRequest.requestURL}")).build<Any>()
    }
}