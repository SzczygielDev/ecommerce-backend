package pl.szczygieldev.shipment.infrastructure.adapter.`in`.http

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/shipment")
@RestController
class ShipmentController {

    @GetMapping
    fun hc(): ResponseEntity<String> {
        return ResponseEntity.ok("OK")
    }
}