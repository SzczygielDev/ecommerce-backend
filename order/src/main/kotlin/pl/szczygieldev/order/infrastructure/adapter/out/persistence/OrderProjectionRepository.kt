package pl.szczygieldev.order.infrastructure.adapter.out.persistence

import org.springframework.stereotype.Repository
import pl.szczygieldev.order.application.port.`in`.query.model.OrderProjection
import pl.szczygieldev.order.application.port.out.OrdersProjections
import pl.szczygieldev.order.domain.CartId
import pl.szczygieldev.order.domain.OrderId
import pl.szczygieldev.order.domain.ParcelId
import pl.szczygieldev.order.domain.PaymentId

@Repository
class OrderProjectionRepository : OrdersProjections {
    private val db = mutableMapOf<String, OrderProjection>()
    override fun findById(id: OrderId): OrderProjection? = db[id.id]

    override fun save(order: OrderProjection) {
        db[order.orderId.id] = order
    }

    override fun findAll(): List<OrderProjection> = db.values.toList()
    override fun findByParcelIdentifier(identifier: ParcelId): OrderProjection? =
        db.values.find { orderProjection -> orderProjection.delivery.parcel?.parcelId == identifier }

    override fun findByPaymentId(paymentId: PaymentId): OrderProjection? =
        db.values.find { orderProjection -> orderProjection.paymentProjection.paymentId.sameValueAs(paymentId) }

    override fun findByCartId(cartId: CartId): OrderProjection? =  db.values.find { orderProjection -> orderProjection.cartId.sameValueAs(cartId)}

}