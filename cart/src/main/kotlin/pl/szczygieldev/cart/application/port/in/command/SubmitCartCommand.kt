package pl.szczygieldev.cart.application.port.`in`.command

import pl.szczygieldev.cart.domain.AppError
import pl.szczygieldev.cart.domain.DeliveryProvider
import pl.szczygieldev.cart.domain.PaymentServiceProvider
import pl.szczygieldev.ecommercelibrary.command.Command

import java.util.UUID

internal data class SubmitCartCommand(val cartId: UUID, val deliveryProvider: DeliveryProvider, val paymentServiceProvider: PaymentServiceProvider)  : Command<AppError>()