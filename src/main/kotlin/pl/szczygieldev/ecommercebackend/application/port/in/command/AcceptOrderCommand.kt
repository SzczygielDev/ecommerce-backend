package pl.szczygieldev.ecommercebackend.application.port.`in`.command

import pl.szczygieldev.ecommercebackend.domain.OrderId
import pl.szczygieldev.shared.architecture.Command

data class AcceptOrderCommand(val orderId: OrderId): Command()