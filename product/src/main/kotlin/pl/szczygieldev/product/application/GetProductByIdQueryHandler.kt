package pl.szczygieldev.product.application

import com.trendyol.kediatr.QueryHandler
import pl.szczygieldev.product.application.port.`in`.query.GetProductByIdQuery
import pl.szczygieldev.product.application.port.out.Products
import pl.szczygieldev.product.domain.Product

internal class GetProductByIdQueryHandler(val products: Products) : QueryHandler<GetProductByIdQuery, Product?> {
    override suspend fun handle(query: GetProductByIdQuery): Product? {
        return products.findById(query.productId)
    }
}