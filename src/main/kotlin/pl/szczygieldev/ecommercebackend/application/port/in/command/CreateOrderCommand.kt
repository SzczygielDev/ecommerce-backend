package pl.szczygieldev.ecommercebackend.application.port.`in`.command

import pl.szczygieldev.ecommercebackend.domain.CartId
import pl.szczygieldev.ecommercebackend.domain.DeliveryProvider
import pl.szczygieldev.ecommercebackend.domain.PaymentServiceProvider
import pl.szczygieldev.shared.architecture.Command

data class CreateOrderCommand(
    val cartId: CartId,
    val paymentServiceProvider: PaymentServiceProvider,
    val deliveryProvider: DeliveryProvider
) : Command()