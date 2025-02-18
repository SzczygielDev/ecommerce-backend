package pl.szczygieldev.cart.infrastructure.adapter.out.persistence.table

import org.jetbrains.exposed.sql.Table

internal object CartProjectionEntryTable : Table("cart_projection_entries")  {
    val id = uuid("id").uniqueIndex().autoGenerate()
    val cartId = reference("cart_id", CartProjectionTable.id)
    val productId = uuid("product_id")
    val quantity = integer("quantity")
}