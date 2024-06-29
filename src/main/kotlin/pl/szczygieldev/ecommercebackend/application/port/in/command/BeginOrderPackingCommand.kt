package pl.szczygieldev.ecommercebackend.application.port.`in`.command

import pl.szczygieldev.ecommercebackend.domain.OrderId

data class BeginOrderPackingCommand(val orderId: OrderId)