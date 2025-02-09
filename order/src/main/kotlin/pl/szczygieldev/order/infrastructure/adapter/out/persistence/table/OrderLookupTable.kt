package pl.szczygieldev.order.infrastructure.adapter.out.persistence.table

import org.jetbrains.exposed.sql.Table

internal object OrderLookupTable: Table("order_lookup") {
    val id = uuid("id").uniqueIndex()
    val paymentId = uuid("payment_id")
}
