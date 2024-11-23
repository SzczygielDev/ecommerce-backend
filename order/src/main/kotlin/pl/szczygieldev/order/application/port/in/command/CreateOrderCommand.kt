package pl.szczygieldev.order.application.port.`in`.command

import pl.szczygieldev.order.application.port.`in`.command.common.Command
import pl.szczygieldev.order.domain.CartId
import pl.szczygieldev.order.domain.DeliveryProvider
import pl.szczygieldev.order.domain.PaymentServiceProvider

data class CreateOrderCommand(
    val cartId: CartId,
    val paymentServiceProvider: PaymentServiceProvider,
    val deliveryProvider: DeliveryProvider
) : Command()