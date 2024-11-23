package pl.szczygieldev.order.application.port.`in`.command

import pl.szczygieldev.order.application.port.`in`.command.common.Command
import pl.szczygieldev.order.domain.PaymentId
import pl.szczygieldev.order.domain.PaymentTransaction

data class ProcessPaymentCommand(val paymentId: PaymentId, val paymentTransaction: PaymentTransaction) : Command()