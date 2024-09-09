package pl.szczygieldev.ecommercebackend.application.port.`in`.command

import pl.szczygieldev.ecommercebackend.application.handlers.common.Command
import pl.szczygieldev.ecommercebackend.domain.OrderId
import pl.szczygieldev.ecommercebackend.domain.ParcelDimensions

data class CompleteOrderPackingCommand (val orderId: OrderId, val dimensions: ParcelDimensions) : Command()