package pl.szczygieldev.product.infrastructure.adapter.out.persistence.table

import org.jetbrains.exposed.sql.Table

internal object ProductTable : Table("products"){
    val id = uuid("id").uniqueIndex()
    val title = varchar("title",255)
    val description = varchar("description",2048)
    val basePrice  = decimal("base_price", 10, 2)
    val imageId = uuid("image_id")
}