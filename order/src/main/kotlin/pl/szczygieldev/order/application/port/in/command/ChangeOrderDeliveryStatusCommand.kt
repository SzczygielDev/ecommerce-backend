package pl.szczygieldev.order.application.port.`in`.command

import pl.szczygieldev.order.application.port.`in`.command.common.Command
import pl.szczygieldev.order.domain.DeliveryStatus
import pl.szczygieldev.order.domain.ParcelId

data class ChangeOrderDeliveryStatusCommand(val parcelId: ParcelId, val status: DeliveryStatus) : Command()