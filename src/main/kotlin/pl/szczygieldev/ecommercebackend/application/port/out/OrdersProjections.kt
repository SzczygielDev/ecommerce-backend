package pl.szczygieldev.ecommercebackend.application.port.out

import pl.szczygieldev.ecommercebackend.application.model.OrderProjection
import pl.szczygieldev.ecommercebackend.domain.CartId
import pl.szczygieldev.ecommercebackend.domain.OrderId
import pl.szczygieldev.ecommercebackend.domain.ParcelId
import pl.szczygieldev.ecommercebackend.domain.PaymentId

interface OrdersProjections {
    fun findById(id : OrderId): OrderProjection?
    fun save(order: OrderProjection)
    fun findAll(): List<OrderProjection>
    fun findByParcelIdentifier(identifier: ParcelId): OrderProjection?
    fun findByPaymentId(paymentId: PaymentId): OrderProjection?
    fun findByCartId(cartId: CartId):OrderProjection?
}