package pl.szczygieldev.external.shipping

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.szczygieldev.external.shipping.model.ParcelDimensions
import pl.szczygieldev.external.shipping.model.ParcelLabelResponse
import pl.szczygieldev.external.shipping.model.RegisterParcelRequest
import pl.szczygieldev.external.shipping.model.RegisterParcelResponse
import java.util.*

@RestController
@RequestMapping(("/external/shipping/"))
class ShippingController(val shippingService: ShippingService) {

    @PostMapping("/register")
    fun registerParcel(@RequestBody request: RegisterParcelRequest): ResponseEntity<*> {
        val parcelId = shippingService.registerParcel(
            ParcelDimensions(
                request.width,
                request.length,
                request.height,
                request.weight
            )
        )

        return ResponseEntity.ok(RegisterParcelResponse(parcelId.toString()))
    }

    @GetMapping("/label/{parcelId}")
    fun getLabel(@PathVariable parcelId: String): ResponseEntity<*> {
        val label = shippingService.getLabel(UUID.fromString(parcelId)) ?: return ResponseEntity.notFound()
            .build<ResponseEntity<*>>()

        return ResponseEntity.ok(ParcelLabelResponse(label.url.toString()))
    }
}