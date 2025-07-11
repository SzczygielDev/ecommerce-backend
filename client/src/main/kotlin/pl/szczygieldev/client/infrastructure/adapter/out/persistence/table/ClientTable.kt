package pl.szczygieldev.client.infrastructure.adapter.out.persistence.table

import org.jetbrains.exposed.sql.Table
import pl.szczygieldev.client.domain.AccountType

internal object  ClientTable : Table("clients") {
    val id = uuid("id").uniqueIndex()
    val externalId = uuid("external_id")
    val name = varchar("name", 50)
    val lastName = varchar("last_name", 50)
    val email = varchar("email", 50)
    val phone = varchar("phone", 50)
    val accountType = enumeration("account_type", AccountType::class)
    val city = varchar("city", 50)
    val zipCode = varchar("zip_code", 50)
    val street = varchar("street", 50)
    val houseNumber = varchar("house_number", 50)
}