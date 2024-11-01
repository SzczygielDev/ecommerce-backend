package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.integration.shipping

import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import pl.szczygieldev.ecommercebackend.application.port.`in`.OrderShippingUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.ChangeOrderDeliveryStatusCommand
import pl.szczygieldev.ecommercebackend.domain.DeliveryStatus
import pl.szczygieldev.ecommercebackend.domain.ParcelId
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.command.MediatorFacade
import pl.szczygieldev.ecommercebackend.infrastructure.integration.shipping.ParcelStatus
import pl.szczygieldev.ecommercebackend.infrastructure.integration.shipping.ParcelStatusChangeNotification

@Component
class MockShippingNotifierService(val mediatorFacade: MediatorFacade) {

    @EventListener
    suspend fun handleParcelStatusChangeNotification(notification: ParcelStatusChangeNotification) {
        mediatorFacade.send(
            ChangeOrderDeliveryStatusCommand(
                ParcelId(notification.parcelId),
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
