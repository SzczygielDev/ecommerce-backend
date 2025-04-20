package pl.szczygieldev.order.application.port.out

import pl.szczygieldev.order.domain.OrderId

internal interface MailService {
    fun sendOrderConfirmationMail(orderId: OrderId): Result<Unit>
}