package pl.szczygieldev.ecommercebackend.application.port.out

import pl.szczygieldev.ecommercebackend.domain.OrderId

interface MailService {
    fun sendOrderConfirmationMail(orderId: OrderId)
}