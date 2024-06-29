package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.integration.shipping

import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import pl.szczygieldev.ecommercebackend.application.port.`in`.OrderPackingUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.ChangeOrderDeliveryStatusCommand
import pl.szczygieldev.ecommercebackend.domain.DeliveryStatus
import pl.szczygieldev.ecommercebackend.domain.ParcelIdentifier
import pl.szczygieldev.ecommercebackend.infrastructure.integration.shipping.ParcelStatus
import pl.szczygieldev.ecommercebackend.infrastructure.integration.shipping.ParcelStatusChangeNotification

@Component
class MockShippingNotifierService(val orderPackingUseCase: OrderPackingUseCase) {

    @EventListener
    fun handleParcelStatusChangeNotification(notification: ParcelStatusChangeNotification) {
        orderPackingUseCase.changeDeliveryStatus(
            ChangeOrderDeliveryStatusCommand(
                ParcelIdentifier(notification.parcelId),
                mapParcelStatus(notification.parcelStatus)
            )
        )
    }

    private fun mapParcelStatus(status: ParcelStatus): DeliveryStatus {
        return when (status) {
            ParcelStatus.PREPARING -> DeliveryStatus.WAITING
            ParcelStatus.RECEIVED, ParcelStatus.RECEIVED_IN_WAREHOUSE, ParcelStatus.IN_DELIVERY -> DeliveryStatus.IN_DELIVERY
            ParcelStatus.DELIVERED -> DeliveryStatus.DELIVERED
        }
    }
}
