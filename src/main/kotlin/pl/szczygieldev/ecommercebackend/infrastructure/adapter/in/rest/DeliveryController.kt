package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.szczygieldev.ecommercebackend.domain.DeliveryProvider

@RequestMapping("/delivery")
@RestController
class DeliveryController {
    @GetMapping("/providers")
    fun getDeliveryMethods(): ResponseEntity<*> = ResponseEntity.ok(DeliveryProvider.values())
}