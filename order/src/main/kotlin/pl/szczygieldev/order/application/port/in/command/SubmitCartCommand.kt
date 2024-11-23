package pl.szczygieldev.order.application.port.`in`.command

import pl.szczygieldev.order.application.port.`in`.command.common.Command
import pl.szczygieldev.order.domain.DeliveryProvider
import pl.szczygieldev.order.domain.PaymentServiceProvider

data class SubmitCartCommand(val cartId: String, val deliveryProvider: DeliveryProvider, val paymentServiceProvider: PaymentServiceProvider)  : Command()