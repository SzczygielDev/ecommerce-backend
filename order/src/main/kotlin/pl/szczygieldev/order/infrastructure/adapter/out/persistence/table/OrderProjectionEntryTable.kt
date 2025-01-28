package pl.szczygieldev.order.infrastructure.adapter.out.persistence.table

import org.jetbrains.exposed.sql.Table

internal object OrderProjectionEntryTable : Table("order_projection_entries") {
    val id = uuid("id").uniqueIndex().autoGenerate()
    val orderId = reference("order_id", OrderProjectionTable.id)
    val productId = uuid("product_id")
    val title = text("title")
    val price = decimal("price", 10, 2)
    val quantity = integer("quantity")
    val imageId = uuid("image_id")
}
