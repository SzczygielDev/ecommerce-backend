package pl.szczygieldev.ecommercebackend.application.port.`in`.command

import pl.szczygieldev.ecommercebackend.application.port.`in`.command.common.Command

data class RemoveItemFromCartCommand(val cartId: String, val productId: String) : Command()