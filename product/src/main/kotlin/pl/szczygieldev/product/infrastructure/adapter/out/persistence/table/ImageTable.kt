package pl.szczygieldev.product.infrastructure.adapter.out.persistence.table

import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

internal object ImageTable : Table("product_images") {
    val id = uuid("id").uniqueIndex()
    val mediaType = varchar("media_type",255)
    val size = long("size")
    val timestamp = timestamp("timestamp").clientDefault { Clock.System.now() }
}