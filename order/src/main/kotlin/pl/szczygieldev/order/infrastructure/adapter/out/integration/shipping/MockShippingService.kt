package pl.szczygieldev.order.infrastructure.adapter.out.integration.shipping

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import pl.szczygieldev.order.application.port.out.ShippingService
import pl.szczygieldev.order.domain.DeliveryProvider
import pl.szczygieldev.order.domain.ParcelDimensions
import pl.szczygieldev.order.domain.ParcelId
import pl.szczygieldev.order.domain.ParcelLabel
import pl.szczygieldev.order.infrastructure.integration.shipping.Parcel
import pl.szczygieldev.order.infrastructure.integration.shipping.ParcelSize
import pl.szczygieldev.order.infrastructure.integration.shipping.ParcelStatus
import pl.szczygieldev.order.infrastructure.integration.shipping.ParcelStatusChangeNotification
import java.net.URL
import java.util.UUID

@Component
class MockShippingService(val eventPublisher: ApplicationEventPublisher) : ShippingService {
    companion object {
        private val log = KotlinLogging.logger { }
    }
    private val db = mutableMapOf<String, Parcel>()
    override fun registerParcel(parcelDimensions: ParcelDimensions, deliveryProvider: DeliveryProvider): ParcelId? {
        val parcel = Parcel(
            UUID.randomUUID().toString(),
            ParcelStatus.PREPARING,
            generateLabel(),
            ParcelSize(
                parcelDimensions.width,
                parcelDimensions.length,
                parcelDimensions.height,
                parcelDimensions.weight
            )
        )

        db.put(
            parcel.id,
            parcel
        )

        return ParcelId(parcel.id)
    }

    override fun getLabel(parcelId: ParcelId): ParcelLabel? = db.get(parcelId.id)?.parcelLabel


    @Scheduled(fixedRate = 1000 * 60)
    private fun processParcels() {
       val parcelsToProcess =  db.values
        parcelsToProcess.forEach { parcel ->
            if(parcel.status!=ParcelStatus.DELIVERED){
                parcel.status=ParcelStatus.entries.get(parcel.status.ordinal+1)
                eventPublisher.publishEvent(ParcelStatusChangeNotification(parcel.id,parcel.status))
            }
        }
    }

    private fun generateLabel(): ParcelLabel = ParcelLabel(URL("http://localhost:8080/"))
}

