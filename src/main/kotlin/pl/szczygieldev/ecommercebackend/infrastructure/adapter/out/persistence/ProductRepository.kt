package pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.persistence

import org.springframework.stereotype.Repository
import pl.szczygieldev.ecommercebackend.application.port.out.Products
import pl.szczygieldev.ecommercebackend.domain.Product
import pl.szczygieldev.ecommercebackend.domain.ProductId
import pl.szczygieldev.ecommercebackend.domain.event.ProductEvent
import pl.szczygieldev.shared.eventstore.EventStore
import java.util.UUID

@Repository
class ProductRepository(val eventStore: EventStore) : Products {
    private val stateTable = mutableMapOf<String, Product>()
    override fun nextIdentity(): ProductId = ProductId(UUID.randomUUID().toString())

    override fun findById(id: ProductId): Product? {
        val snapshot = stateTable[id.id()] ?: return null
        val product = Product.fromSnapshot(
            snapshot.productId,
            snapshot.title,
            snapshot.description,
            snapshot.basePrice
        )

        val events = eventStore.getEvents<ProductEvent>(product.productId) ?: return null
        product.applyEvents(events)

        return product
    }

    override fun findAll(): List<Product> {
        return stateTable.values.toList().map{
            val product = Product.fromSnapshot(
                it.productId,
                it.title,
                it.description,
                it.basePrice
            )

            val events =
                eventStore.getEvents<ProductEvent>(product.productId) ?: return@map null
            product.applyEvents(events)
            product
        }.filterNotNull()
    }

    override fun save(product: Product, version: Int): Product {
        stateTable[product.productId.id()] = product
        eventStore.appendEvents(product.productId, product.occurredEvents(), version)
        product.clearOccurredEvents()
        return product
    }

    override fun delete(productId: ProductId): Product? {
        return stateTable.remove(productId.id())
    }
}