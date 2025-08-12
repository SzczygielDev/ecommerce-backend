package pl.szczygieldev.client.infrastructure.adapter.`in`.http

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.annotation.*
import pl.szczygieldev.client.domain.*
import pl.szczygieldev.client.infrastructure.adapter.`in`.http.presenter.ClientPresenter
import pl.szczygieldev.client.infrastructure.adapter.`in`.http.resource.ClientCreateRequest
import pl.szczygieldev.client.infrastructure.adapter.`in`.http.resource.ClientDto
import pl.szczygieldev.client.infrastructure.adapter.`in`.http.resource.ClientUpdateRequest
import pl.szczygieldev.client.infrastructure.adapter.out.persistence.ClientRepository
import java.net.URI
import java.security.Principal
import java.util.*

@RequestMapping("/clients")
@RestController
class ClientController(val clientRepository: ClientRepository, val clientPresenter: ClientPresenter) {

    @GetMapping("/current")
    fun getCurrentClient(principal: Principal): ResponseEntity<*> {
        val client =
            clientRepository.findByExternalId(UUID.fromString(principal.name)) ?: return ResponseEntity.notFound()
                .build<Any>()

        return ResponseEntity.ok().body(clientPresenter.toDto(client))
    }

    @GetMapping
    fun getClients(
        principal: Principal,
        @RequestParam(required = false) offset: Long?,
        @RequestParam(required = false) limit: Int?
    ): ResponseEntity<List<ClientDto>> {
        if(offset != null && limit != null) {
            val clients =
                clientRepository.findPage(offset, limit)
            return ResponseEntity.ok().body(clients.map { client -> clientPresenter.toDto(client) })
        }

        return ResponseEntity.ok().body(clientRepository.findAll().map { client -> clientPresenter.toDto(client) })

    }

    @PostMapping
    fun createClient(
        @RequestBody request: ClientCreateRequest,
        principal: Principal,
        servletRequest: HttpServletRequest
    ): ResponseEntity<*> {
        if (principal !is JwtAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build<Any>()
        }

        val jwt = principal.token
        val email = jwt.claims["email"] as String?

        if (email == null) {
            return ResponseEntity.badRequest().build<Any>()
        }

        val client = Client(
            ClientId(UUID.randomUUID()),
            UUID.fromString(principal.name),
            request.name, request.lastName,
            ClientEmail(email),
            ClientPhoneNumber(request.phone),
            AccountType.STANDARD,
            ClientAddress(request.city, request.zipCode, request.street, request.houseNumber)
        )

        clientRepository.save(client)

        return ResponseEntity.created(URI.create("${servletRequest.requestURL}")).build<Any>()
    }

    @PutMapping
    fun updateClient(
        @RequestBody request: ClientUpdateRequest,
        principal: Principal,
    ): ResponseEntity<*> {
        val client =
            clientRepository.findByExternalId(UUID.fromString(principal.name)) ?: return ResponseEntity.notFound()
                .build<Any>()

        val updatedClient = client.copy(
            name = request.name,
            lastName = request.lastName,
            phone = ClientPhoneNumber(request.phone),
            address = client.address.copy(
                city = request.city,
                zipCode = request.zipCode,
                street = request.street,
                houseNumber = request.houseNumber
            )
        )

        clientRepository.save(updatedClient)

        return ResponseEntity.ok().body(clientPresenter.toDto(updatedClient))
    }

    @PutMapping("/premium")
    fun premiumActivation(principal: Principal): ResponseEntity<*> {
        val client =
            clientRepository.findByExternalId(UUID.fromString(principal.name)) ?: return ResponseEntity.notFound()
                .build<Any>()

        client.activatePremiumAccount()

        clientRepository.save(client)

        return ResponseEntity.ok(clientPresenter.toDto(client))
    }

    @DeleteMapping("/premium")
    fun premiumDeactivation(principal: Principal): ResponseEntity<*> {
        val client =
            clientRepository.findByExternalId(UUID.fromString(principal.name)) ?: return ResponseEntity.notFound()
                .build<Any>()

        client.deactivatePremiumAccount()

        clientRepository.save(client)

        return ResponseEntity.ok(clientPresenter.toDto(client))
    }
}