package pl.szczygieldev.order.infrastructure.adapter.out.persistence.table

import org.jetbrains.exposed.sql.Table
import pl.szczygieldev.order.domain.PaymentServiceProvider
import pl.szczygieldev.order.domain.PaymentStatus

internal object OrderProjectionPaymentTable : Table("order_projection_payments"){
    val id = uuid("id").uniqueIndex()
    val amount = decimal("amount", 10, 2)
    val amountPaid = decimal("amount_paid", 10, 2)
    val paymentServiceProvider = enumeration("payment_service_provider", PaymentServiceProvider::class)
    val status = enumeration("status", PaymentStatus::class)
    val url = varchar("url", 255)
}