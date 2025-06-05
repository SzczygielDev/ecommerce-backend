package pl.szczygieldev.cart.infrastructure.adapter.out.persistence.table

import org.jetbrains.exposed.sql.Table
import pl.szczygieldev.cart.domain.CartStatus


internal object CartProjectionTable : Table("cart_projections")  {
    val id = uuid("id").uniqueIndex()
    val status = enumeration("status", CartStatus::class)
    val amount = decimal("amount",10,2)
}