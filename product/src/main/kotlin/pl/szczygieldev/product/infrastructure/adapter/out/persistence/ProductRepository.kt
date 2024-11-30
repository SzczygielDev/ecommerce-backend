package pl.szczygieldev.product.infrastructure.adapter.out.persistence

import org.springframework.stereotype.Repository
import pl.szczygieldev.ecommercelibrary.eventstore.EventStore
import pl.szczygieldev.product.application.port.out.Products
import pl.szczygieldev.product.domain.Product
import pl.szczygieldev.product.domain.ProductId
import pl.szczygieldev.product.domain.event.ProductEvent
import java.util.UUID

@Repository("productModule.ProductRepository")
class ProductRepository(val eventStore: EventStore) : Products {
    private val stateTable = mutableMapOf<String, Product>()
    override fun nextIdentity(): ProductId = ProductId(UUID.randomUUID().toString())

    override fun findById(id: ProductId): Product? {
        val snapshot = stateTable[id.id()] ?: return null
        val product = Product.fromSnapshot(
            snapshot.productId,
            snapshot.title,
            snapshot.description,
            snapshot.basePrice,
            snapshot.imageId
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
                it.basePrice,
                it.imageId
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