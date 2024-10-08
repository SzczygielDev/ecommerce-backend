package pl.szczygieldev.ecommercebackend.application

import pl.szczygieldev.ecommercebackend.application.port.`in`.OrderMailUseCase
import pl.szczygieldev.ecommercebackend.application.port.out.MailService
import pl.szczygieldev.ecommercebackend.domain.OrderId
import pl.szczygieldev.shared.architecture.UseCase

@UseCase
class OrderMailService(private val mailService: MailService) : OrderMailUseCase {
    override fun sendConfirmationMail(orderId: OrderId) {
        mailService.sendOrderConfirmationMail(orderId)
    }
}