package pl.szczygieldev.product.application.port.`in`.query

import com.trendyol.kediatr.Query
import pl.szczygieldev.product.domain.Product

internal data class GetPaginatedProductsQuery(
    val offset: Long,
    val limit: Int
) : Query<List<Product>>