package pl.szczygieldev.order.application.port.`in`.command

import pl.szczygieldev.ecommercelibrary.command.Command
import pl.szczygieldev.ecommercelibrary.command.CommandError
import pl.szczygieldev.order.domain.DeliveryStatus
import pl.szczygieldev.order.domain.OrderId
import pl.szczygieldev.order.domain.ParcelId

internal data class ChangeOrderDeliveryStatusCommand(val parcelId: ParcelId, val status: DeliveryStatus) :
    Command<ChangeOrderDeliveryStatusCommand.Error>() {
    internal sealed class Error(override val message: String, override val code: String) :
        CommandError(message, code)

    internal data class OrderNotFoundError(override val message: String, override val code: String) :
        Error(message, code) {
        companion object {
            fun forId(id: OrderId): OrderNotFoundError {
                return OrderNotFoundError("Cannot find order with id='${id.id()}'.", "COD-1")
            }

            fun forParcelId(parcelId: ParcelId): OrderNotFoundError {
                return OrderNotFoundError("Cannot find order with parcel id='${parcelId.id}'.", "COD-1")
            }
        }
    }
}