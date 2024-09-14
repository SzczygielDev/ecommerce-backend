package pl.szczygieldev.ecommercebackend.application.port.out

import pl.szczygieldev.ecommercebackend.domain.Order
import pl.szczygieldev.ecommercebackend.domain.OrderId

interface Orders {
    fun nextIdentity(): OrderId
    fun findById(id : OrderId): Order?
    fun save(order: Order, version: Int)
}