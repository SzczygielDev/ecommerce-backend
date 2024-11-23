package pl.szczygieldev.order.application.port.`in`.command

import pl.szczygieldev.order.application.port.`in`.command.common.Command
import pl.szczygieldev.order.domain.CartId

data class CalculateCartTotalCommand(val cartId: CartId) : Command()