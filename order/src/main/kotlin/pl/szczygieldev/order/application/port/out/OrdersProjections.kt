package pl.szczygieldev.order.application.port.out

import pl.szczygieldev.order.application.port.`in`.query.model.OrderProjection
import pl.szczygieldev.order.domain.CartId
import pl.szczygieldev.order.domain.OrderId
import pl.szczygieldev.order.domain.ParcelId
import pl.szczygieldev.order.domain.PaymentId


interface OrdersProjections {
    fun findById(id : OrderId): OrderProjection?
    fun save(order: OrderProjection)
    fun findAll(): List<OrderProjection>
    fun findByParcelIdentifier(identifier: ParcelId): OrderProjection?
    fun findByPaymentId(paymentId: PaymentId): OrderProjection?
    fun findByCartId(cartId: CartId): OrderProjection?
}