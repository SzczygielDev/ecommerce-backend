package pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.integration.shipping

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import pl.szczygieldev.ecommercebackend.application.port.out.ShippingService
import pl.szczygieldev.ecommercebackend.domain.ParcelDimensions
import pl.szczygieldev.ecommercebackend.domain.ParcelIdentifier
import pl.szczygieldev.ecommercebackend.domain.ParcelLabel
import pl.szczygieldev.ecommercebackend.infrastructure.integration.shipping.Parcel
import pl.szczygieldev.ecommercebackend.infrastructure.integration.shipping.ParcelSize
import pl.szczygieldev.ecommercebackend.infrastructure.integration.shipping.ParcelStatus
import pl.szczygieldev.ecommercebackend.infrastructure.integration.shipping.ParcelStatusChangeNotification
import java.net.URL
import java.util.UUID

@Component
class MockShippingService(val eventPublisher: ApplicationEventPublisher) : ShippingService {
    companion object {
        private val log = KotlinLogging.logger { }
    }
    private val db = mutableMapOf<String, Parcel>()
    override fun registerParcel(parcelDimensions: ParcelDimensions): ParcelIdentifier {
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

        return ParcelIdentifier(parcel.id)
    }

    override fun getLabel(parcelIdentifier: ParcelIdentifier): ParcelLabel? = db.get(parcelIdentifier.identifier)?.parcelLabel


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

