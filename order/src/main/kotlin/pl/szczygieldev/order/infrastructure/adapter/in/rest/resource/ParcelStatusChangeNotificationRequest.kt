package pl.szczygieldev.order.infrastructure.adapter.`in`.rest.resource

import java.util.UUID

internal data class ParcelStatusChangeNotificationRequest(val parcelId: UUID, val parcelStatus: ParcelStatus)