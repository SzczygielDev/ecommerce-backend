package pl.szczygieldev.ecommercebackend.application.port.`in`.command

import pl.szczygieldev.ecommercebackend.application.port.`in`.command.common.Command
import pl.szczygieldev.ecommercebackend.domain.DeliveryProvider
import pl.szczygieldev.ecommercebackend.domain.PaymentServiceProvider

data class SubmitCartCommand(val cartId: String, val deliveryProvider: DeliveryProvider, val paymentServiceProvider: PaymentServiceProvider)  : Command()