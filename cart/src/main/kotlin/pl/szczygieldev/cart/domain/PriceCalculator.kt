package pl.szczygieldev.cart.domain

import pl.szczygieldev.ecommercelibrary.ddd.core.DomainService
import java.math.BigDecimal

@DomainService
internal class PriceCalculator {
    fun calculate(cart: Cart, products: List<Product>): BigDecimal? {
        return cart.items.sumOf { item ->

            val productForItem = products.firstOrNull { product -> product.productId == item.productId } ?: return null

            productForItem.price.multiply(BigDecimal(item.quantity))
        }
    }
}