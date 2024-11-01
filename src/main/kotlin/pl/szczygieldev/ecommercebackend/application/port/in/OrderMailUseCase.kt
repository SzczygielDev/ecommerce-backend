package pl.szczygieldev.ecommercebackend.application.port.`in`

import pl.szczygieldev.ecommercebackend.application.port.`in`.command.SendOrderConfirmationMailCommand

interface OrderMailUseCase {
    fun sendConfirmationMail(command: SendOrderConfirmationMailCommand)
}