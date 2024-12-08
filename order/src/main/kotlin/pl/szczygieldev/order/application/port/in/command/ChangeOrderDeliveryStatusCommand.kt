package pl.szczygieldev.order.application.port.`in`.command

import pl.szczygieldev.ecommercelibrary.command.Command
import pl.szczygieldev.order.domain.DeliveryStatus
import pl.szczygieldev.order.domain.ParcelId
import pl.szczygieldev.order.domain.error.AppError

data class ChangeOrderDeliveryStatusCommand(val parcelId: ParcelId, val status: DeliveryStatus) : Command<AppError>()