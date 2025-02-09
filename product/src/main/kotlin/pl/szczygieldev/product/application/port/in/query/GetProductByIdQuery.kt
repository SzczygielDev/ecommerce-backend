package pl.szczygieldev.product.application.port.`in`.query

import com.trendyol.kediatr.Query
import pl.szczygieldev.product.domain.Product
import pl.szczygieldev.product.domain.ProductId

internal data  class GetProductByIdQuery(val productId: ProductId) : Query<Product?>