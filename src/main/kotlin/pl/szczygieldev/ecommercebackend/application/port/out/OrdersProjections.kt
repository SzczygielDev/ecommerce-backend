package pl.szczygieldev.ecommercebackend.application.port.out

import pl.szczygieldev.ecommercebackend.application.model.OrderProjection
import pl.szczygieldev.ecommercebackend.domain.OrderId
import pl.szczygieldev.ecommercebackend.domain.ParcelIdentifier

interface OrdersProjections {
    fun findById(id : OrderId): OrderProjection?
    fun save(order: OrderProjection)
    fun findAll(): List<OrderProjection>
    fun findByParcelIdentifier(identifier: ParcelIdentifier): OrderProjection?
}