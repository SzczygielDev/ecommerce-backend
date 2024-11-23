package pl.szczygieldev.order.infrastructure.adapter.`in`.rest

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.szczygieldev.order.domain.DeliveryProvider
import pl.szczygieldev.order.infrastructure.mapper.DeliveryProviderMapper

@RequestMapping("/delivery")
@RestController
class DeliveryController(val deliveryProviderMapper: DeliveryProviderMapper) {
    @GetMapping("/providers")
    fun getDeliveryMethods(): ResponseEntity<*> = ResponseEntity.ok(
        DeliveryProvider.values().map { deliveryProvider -> deliveryProviderMapper.mapToDetails(deliveryProvider) })
}