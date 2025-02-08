package pl.szczygieldev.product

import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component
import pl.szczygieldev.ecommercelibrary.command.Mediator
import pl.szczygieldev.product.application.port.`in`.query.GetAllProductsQuery
import pl.szczygieldev.product.application.port.`in`.query.GetProductByIdQuery
import pl.szczygieldev.product.domain.ProductId
import java.util.UUID

@Component
internal class ProductFacadeImpl(val mediator: Mediator) : ProductFacade {
    override fun findAll(): List<ProductProjection> = runBlocking {
        mediator.send(GetAllProductsQuery()).map { product ->
            ProductProjection(
                product.productId.id(),
                product.title.value,
                product.description.content,
                product.price.amount,
                product.priceChanges.map { priceChange ->
                    ProductProjection.ProductPriceChangeProjection(
                        priceChange.newPrice.amount,
                        priceChange.timestamp
                    )
                },
                product.imageId.id
            )
        }
    }

    override fun findById(id: UUID): ProductProjection? = runBlocking {
        val product = mediator.send(GetProductByIdQuery(ProductId(id))) ?: return@runBlocking null

        return@runBlocking ProductProjection(
            product.productId.id(),
            product.title.value,
            product.description.content,
            product.price.amount,
            product.priceChanges.map { priceChange ->
                ProductProjection.ProductPriceChangeProjection(
                    priceChange.newPrice.amount,
                    priceChange.timestamp
                )
            },
            product.imageId.id
        )
    }
}