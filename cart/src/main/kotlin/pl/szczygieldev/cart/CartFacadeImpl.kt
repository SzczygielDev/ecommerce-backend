package pl.szczygieldev.cart

import org.springframework.stereotype.Component
import pl.szczygieldev.cart.application.port.out.CartsProjections
import pl.szczygieldev.cart.domain.CartId
import java.util.*

@Component("cartModule.FacadeImpl")
internal class CartFacadeImpl(val cartsProjections: CartsProjections) : CartFacade {
    override fun findById(id: UUID): CartProjection? {
        return cartsProjections.findById(CartId(id))
    }
}