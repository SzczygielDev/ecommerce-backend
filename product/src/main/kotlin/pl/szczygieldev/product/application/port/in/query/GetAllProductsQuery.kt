package pl.szczygieldev.product.application.port.`in`.query

import com.trendyol.kediatr.Query
import pl.szczygieldev.product.domain.Product

internal class GetAllProductsQuery : Query<List<Product>>