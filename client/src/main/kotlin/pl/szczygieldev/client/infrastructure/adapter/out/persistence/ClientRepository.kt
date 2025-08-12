package pl.szczygieldev.client.infrastructure.adapter.out.persistence

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert
import org.springframework.stereotype.Repository
import pl.szczygieldev.client.domain.*
import pl.szczygieldev.client.infrastructure.adapter.out.persistence.table.ClientTable
import java.util.*

@Repository
class ClientRepository {
    fun save(client: Client) = transaction {
        ClientTable.upsert {
            it[id] = client.id.idAsUUID()
            it[externalId] = client.externalId
            it[name] = client.name
            it[lastName] = client.lastName
            it[email] = client.email.value
            it[phone] = client.phone.value
            it[accountType] = client.accountType
            it[city] = client.address.city
            it[zipCode] = client.address.zipCode
            it[street] = client.address.street
            it[houseNumber] = client.address.houseNumber
        }
    }

    fun findById(id: ClientId): Client? = transaction {
        val rows =
            ClientTable.selectAll().where(ClientTable.id.eq(id.idAsUUID())).singleOrNull() ?: return@transaction null

        return@transaction mapClient(rows)
    }

    fun findByExternalId(externalId: UUID): Client? = transaction {
        val rows =
            ClientTable.selectAll().where(ClientTable.externalId.eq(externalId)).singleOrNull()
                ?: return@transaction null

        return@transaction mapClient(rows)
    }

    fun findPage(offset: Long, limit: Int): List<Client> = transaction {
        return@transaction ClientTable.selectAll().offset(offset).limit(limit).map { result ->
            mapClient(result)
        }
    }

    fun findAll(): List<Client> = transaction {
        return@transaction ClientTable.selectAll().map { result ->
            mapClient(result)
        }
    }

    private fun mapClient(rows: ResultRow): Client = Client(
        ClientId(rows[ClientTable.id]),
        rows[ClientTable.externalId],
        rows[ClientTable.name],
        rows[ClientTable.lastName],
        ClientEmail(rows[ClientTable.email]),
        ClientPhoneNumber(rows[ClientTable.phone]),
        rows[ClientTable.accountType],
        ClientAddress(
            rows[ClientTable.city],
            rows[ClientTable.zipCode],
            rows[ClientTable.street],
            rows[ClientTable.houseNumber]
        )
    )
}