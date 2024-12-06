package pl.szczygieldev.order.application.port.`in`.command

import pl.szczygieldev.ecommercelibrary.command.Command

data class RemoveItemFromCartCommand(val cartId: String, val productId: String) : Command()