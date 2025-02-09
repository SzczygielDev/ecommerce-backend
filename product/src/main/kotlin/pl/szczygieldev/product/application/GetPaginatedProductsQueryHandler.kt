package pl.szczygieldev.product.application

import com.trendyol.kediatr.QueryHandler
import pl.szczygieldev.product.application.port.`in`.query.GetPaginatedProductsQuery
import pl.szczygieldev.product.application.port.out.Products
import pl.szczygieldev.product.domain.Product

internal class GetPaginatedProductsQueryHandler(val products: Products) : QueryHandler<GetPaginatedProductsQuery, List<Product>> {
    override suspend fun handle(query: GetPaginatedProductsQuery): List<Product> {
        return products.findPage(query.offset,query.limit)
    }
}