package pl.szczygieldev.ecommercebackend.application.port.`in`.command

import pl.szczygieldev.ecommercebackend.domain.CartId

data class CalculateCartTotalCommand(val cartId: CartId)