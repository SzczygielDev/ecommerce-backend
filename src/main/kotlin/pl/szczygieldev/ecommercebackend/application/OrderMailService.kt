package pl.szczygieldev.ecommercebackend.application

import pl.szczygieldev.ecommercebackend.application.port.`in`.OrderMailUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.SendOrderConfirmationMailCommand
import pl.szczygieldev.ecommercebackend.application.port.out.MailService
import pl.szczygieldev.ecommercebackend.domain.OrderId
import pl.szczygieldev.shared.architecture.UseCase

@UseCase
class OrderMailService(private val mailService: MailService) : OrderMailUseCase {
    override fun sendConfirmationMail(command: SendOrderConfirmationMailCommand) {
        mailService.sendOrderConfirmationMail(command.orderId)
    }
}