package pl.szczygieldev.order.application.port.out

import pl.szczygieldev.order.domain.Order
import pl.szczygieldev.order.domain.OrderId

interface Orders {
    fun nextIdentity(): OrderId
    fun findById(id : OrderId): Order?
    fun save(order: Order, version: Int)
}