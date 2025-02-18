package pl.szczygieldev.order.infrastructure.adapter.out.integration.cart

import org.springframework.stereotype.Repository
import pl.szczygieldev.cart.CartFacade
import pl.szczygieldev.order.application.port.out.Carts
import pl.szczygieldev.order.domain.Cart
import pl.szczygieldev.order.domain.CartId
import pl.szczygieldev.order.domain.ProductId

@Repository
internal class CartRepository(val cartFacade: CartFacade) : Carts {
    override fun findById(id: CartId): Cart? {
        val found = cartFacade.findById(id.id) ?: return null

        return Cart(
            CartId(found.cartId),
            found.items.map { item -> Cart.Item(ProductId(item.productId), item.quantity) },
            found.amount
        )
    }
}