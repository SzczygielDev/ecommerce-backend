package pl.szczygieldev.product.application.port.`in`.query

import com.trendyol.kediatr.Query
import pl.szczygieldev.product.domain.Product

data internal class GetPaginatedProductsQuery(val offset: Long,
                                val limit: Int) : Query<List<Product>>