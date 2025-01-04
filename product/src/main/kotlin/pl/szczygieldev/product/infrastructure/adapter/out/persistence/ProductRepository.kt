package pl.szczygieldev.product.infrastructure.adapter.out.persistence

import org.springframework.stereotype.Repository
import pl.szczygieldev.ecommercelibrary.eventstore.EventStore
import pl.szczygieldev.product.application.port.out.Products
import pl.szczygieldev.product.domain.event.ProductEvent
import pl.szczygieldev.product.infrastructure.adapter.out.persistence.table.ProductTable
import java.util.UUID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import pl.szczygieldev.product.domain.*
import pl.szczygieldev.product.domain.Product
import pl.szczygieldev.product.domain.ProductDescription
import pl.szczygieldev.product.domain.ProductId
import pl.szczygieldev.product.domain.ProductPrice
import pl.szczygieldev.product.domain.ProductTitle

@Repository("productModule.ProductRepository")
internal class ProductRepository(val eventStore: EventStore) : Products {
    override fun nextIdentity(): ProductId = ProductId(UUID.randomUUID())

    override fun findById(id: ProductId): Product? = transaction {
        val result = ProductTable.selectAll().where(ProductTable.id.eq(id.idAsUUID())).singleOrNull() ?: return@transaction null

        val product = Product.fromSnapshot(
            ProductId(result[ProductTable.id]),
            ProductTitle(result[ProductTable.title]),
            ProductDescription(result[ProductTable.description]),
            ProductPrice(result[ProductTable.basePrice]),
            ImageId(result[ProductTable.imageId].toString())
        )
        val events = eventStore.getEvents<ProductEvent>(product.productId) ?: return@transaction null
        product.applyEvents(events)
        return@transaction product
    }

    override fun findAll(): List<Product> = transaction {
        return@transaction ProductTable.selectAll().map { result ->
            val product = Product.fromSnapshot(
                ProductId(result[ProductTable.id]),
                ProductTitle(result[ProductTable.title]),
                ProductDescription(result[ProductTable.description]),
                ProductPrice(result[ProductTable.basePrice]),
                ImageId(result[ProductTable.imageId].toString())
            )
            val events =
                eventStore.getEvents<ProductEvent>(product.productId) ?: return@map null
            product.applyEvents(events)
            product
        }.filterNotNull()
    }

    override fun save(product: Product, version: Int): Product = transaction {
        ProductTable.upsert {
            it[id] = product.productId.id
            it[title] = product.title.value
            it[description] = product.description.content
            it[basePrice] = product.price.amount
            it[imageId] = UUID.fromString(product.imageId.id)
        }
        eventStore.appendEvents(product.productId, product.occurredEvents(), version)
        product.clearOccurredEvents()
        return@transaction product
    }

    override fun delete(productId: ProductId): Boolean = transaction {
        return@transaction ProductTable.deleteWhere { id.eq(productId.idAsUUID()) } == 1
    }

    override fun findPage(offset: Long, limit: Int): List<Product> = transaction {
        return@transaction ProductTable.selectAll().offset(offset).limit(limit).map { result ->
            val product = Product.fromSnapshot(
                ProductId(result[ProductTable.id]),
                ProductTitle(result[ProductTable.title]),
                ProductDescription(result[ProductTable.description]),
                ProductPrice(result[ProductTable.basePrice]),
                ImageId(result[ProductTable.imageId].toString())
            )
            val events =
                eventStore.getEvents<ProductEvent>(product.productId) ?: return@map null
            product.applyEvents(events)
            product
        }.filterNotNull()
    }
}