package pl.szczygieldev.order.application.port.out

import pl.szczygieldev.order.domain.Order
import pl.szczygieldev.order.domain.OrderId
import pl.szczygieldev.order.domain.PaymentId

internal interface Orders {
    fun nextIdentity(): OrderId
    fun findById(id : OrderId): Order?
    fun save(order: Order, version: Int)
    fun findByPaymentId(paymentId: PaymentId): Order?
}