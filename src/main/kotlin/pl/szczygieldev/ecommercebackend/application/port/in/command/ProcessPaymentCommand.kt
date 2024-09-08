package pl.szczygieldev.ecommercebackend.application.port.`in`.command

import pl.szczygieldev.ecommercebackend.application.handlers.common.Command
import pl.szczygieldev.ecommercebackend.domain.PaymentId
import pl.szczygieldev.ecommercebackend.domain.PaymentTransaction

data class ProcessPaymentCommand(val paymentId: PaymentId, val paymentTransaction: PaymentTransaction) : Command()