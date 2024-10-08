package pl.szczygieldev.ecommercebackend.application.port.`in`

import pl.szczygieldev.ecommercebackend.domain.OrderId

interface OrderMailUseCase {
    fun sendConfirmationMail(orderId: OrderId)
}