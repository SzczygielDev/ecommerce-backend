package pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.persistence

import org.springframework.stereotype.Repository
import pl.szczygieldev.ecommercebackend.application.model.OrderProjection
import pl.szczygieldev.ecommercebackend.application.port.out.OrdersProjections
import pl.szczygieldev.ecommercebackend.domain.OrderId

@Repository
class OrderProjectionRepository : OrdersProjections {
    private val db = mutableMapOf<String, OrderProjection>()
    override fun findById(id: OrderId): OrderProjection?   = db[id.id]

    override fun save(order: OrderProjection) {
        db[order.orderId.id] = order
    }

    override fun findAll(): List<OrderProjection>  =  db.values.toList()
}