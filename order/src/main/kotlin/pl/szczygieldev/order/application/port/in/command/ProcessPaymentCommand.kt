package pl.szczygieldev.order.application.port.`in`.command

import pl.szczygieldev.ecommercelibrary.command.Command
import pl.szczygieldev.order.domain.PaymentId
import pl.szczygieldev.order.domain.PaymentTransaction
import pl.szczygieldev.order.domain.error.AppError

data class ProcessPaymentCommand(val paymentId: PaymentId, val paymentTransaction: PaymentTransaction) : Command<AppError>()