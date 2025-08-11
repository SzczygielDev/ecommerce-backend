package pl.szczygieldev.order.infrastructure.adapter.`in`.http

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.szczygieldev.ecommercelibrary.command.Mediator
import pl.szczygieldev.order.application.port.`in`.command.ChangeOrderDeliveryStatusCommand

import pl.szczygieldev.order.domain.DeliveryStatus
import pl.szczygieldev.order.domain.ParcelId
import pl.szczygieldev.order.infrastructure.adapter.`in`.http.presenter.DeliveryProviderPresenter
import pl.szczygieldev.order.infrastructure.adapter.`in`.http.resource.ParcelStatus
import pl.szczygieldev.order.infrastructure.adapter.`in`.http.resource.ParcelStatusChangeNotificationRequest
import pl.szczygieldev.shipmentsdk.model.DeliveryProvider

@RequestMapping("/delivery")
@RestController
internal class DeliveryController(val deliveryProviderPresenter: DeliveryProviderPresenter, val mediator: Mediator) {
    @GetMapping("/providers")
    fun getDeliveryMethods(): ResponseEntity<*> {

        return ResponseEntity.ok(
            DeliveryProvider.values().map { deliveryProviderPresenter.toDto(it) }
        )
    }

    @PostMapping("/notification")
    suspend fun notificationHook(@RequestBody notification: ParcelStatusChangeNotificationRequest): ResponseEntity<*> {
        mediator.send(
            ChangeOrderDeliveryStatusCommand(
                ParcelId(notification.parcelId),
                mapParcelStatus(notification.parcelStatus)
            )
        )

        return ResponseEntity.ok("OK")
    }

    private fun mapParcelStatus(status: ParcelStatus): DeliveryStatus {
        return when (status) {
            ParcelStatus.PREPARING -> DeliveryStatus.WAITING
            ParcelStatus.RECEIVED, ParcelStatus.RECEIVED_IN_WAREHOUSE, ParcelStatus.IN_DELIVERY -> DeliveryStatus.IN_DELIVERY
            ParcelStatus.DELIVERED -> DeliveryStatus.DELIVERED
        }
    }
}