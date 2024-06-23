package pl.szczygieldev.ecommercebackend.application.port.`in`.command

import pl.szczygieldev.ecommercebackend.domain.OrderId

data class RejectOrderCommand(val orderId: OrderId)