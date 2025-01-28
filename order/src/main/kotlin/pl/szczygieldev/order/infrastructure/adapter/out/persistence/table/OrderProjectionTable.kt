package pl.szczygieldev.order.infrastructure.adapter.out.persistence.table

import org.jetbrains.exposed.sql.Table
import pl.szczygieldev.order.domain.OrderStatus
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import pl.szczygieldev.order.domain.DeliveryProvider
import pl.szczygieldev.order.domain.DeliveryStatus

internal object OrderProjectionTable : Table("order_projections")  {
    val id = uuid("id").uniqueIndex()
    val cartId = uuid("cart_id")
    val status = enumeration("status", OrderStatus::class)
    val paymentId = reference("payment_id",OrderProjectionPaymentTable.id)
    val deliveryProvider = enumeration("delivery_provider", DeliveryProvider::class)
    val deliveryStatus = enumeration("delivery_status", DeliveryStatus::class)
    val parcelId = uuid("parcel_id").nullable()
    val width = double("parcel_width").nullable()
    val length = double("parcel_length").nullable()
    val height = double("parcel_height").nullable()
    val weight = double("parcel_weight").nullable()
    val createdAt = timestamp("timestamp")
}