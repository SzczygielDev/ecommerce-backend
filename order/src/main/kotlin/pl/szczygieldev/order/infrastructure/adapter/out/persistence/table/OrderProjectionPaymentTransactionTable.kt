package pl.szczygieldev.order.infrastructure.adapter.out.persistence.table

import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

internal object OrderProjectionPaymentTransactionTable : Table("order_projection_payment_transactions") {
    val id = uuid("id").uniqueIndex()
    val paymentId = reference("payment_id", OrderProjectionPaymentTable.id)
    val amount = decimal("amount", 10, 2)
    val datetime = timestamp("timestamp")
}