package pl.szczygieldev.product.application

import com.trendyol.kediatr.QueryHandler
import pl.szczygieldev.product.application.port.`in`.query.GetAllProductsQuery
import pl.szczygieldev.product.application.port.out.Products
import pl.szczygieldev.product.domain.Product

internal class GetAllProductsQueryHandler(val products: Products) : QueryHandler<GetAllProductsQuery,List<Product>> {
    override suspend fun handle(query: GetAllProductsQuery): List<Product> {
        return products.findAll()
    }
}