package pl.szczygieldev.ecommercebackend.application.port.`in`.command

import pl.szczygieldev.ecommercebackend.application.handlers.common.Command
import pl.szczygieldev.ecommercebackend.domain.DeliveryStatus
import pl.szczygieldev.ecommercebackend.domain.ParcelIdentifier

data class ChangeOrderDeliveryStatusCommand(val parcelIdentifier: ParcelIdentifier, val status: DeliveryStatus) : Command()