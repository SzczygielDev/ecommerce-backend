package pl.szczygieldev.ecommercebackend.application

import pl.szczygieldev.ddd.core.DomainEventPublisher
import pl.szczygieldev.ecommercebackend.application.port.`in`.PriceCalculatorUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.CalculateCartTotalCommand
import pl.szczygieldev.ecommercebackend.application.port.out.CartsProjections
import pl.szczygieldev.ecommercebackend.application.port.out.Products
import pl.szczygieldev.ecommercebackend.domain.Product
import pl.szczygieldev.ecommercebackend.domain.ProductId
import pl.szczygieldev.ecommercebackend.domain.event.CartTotalRecalculated
import pl.szczygieldev.ecommercebackend.domain.event.PriceCalculatorEvent
import pl.szczygieldev.ecommercebackend.domain.exception.CartNotFoundException
import pl.szczygieldev.shared.architecture.UseCase
import java.lang.RuntimeException

@UseCase
private class PriceCalculatorService(
    val products: Products,
    val cartProjections: CartsProjections,
    val priceCalculatorEventPublisher: DomainEventPublisher<PriceCalculatorEvent>,
) : PriceCalculatorUseCase {
    override fun calculateCartTotal(command: CalculateCartTotalCommand) {
        val cartId = command.cartId
        val cartProjection = cartProjections.findById(cartId) ?: throw CartNotFoundException(cartId)

        val foundProducts = mutableMapOf<ProductId, Product?>()

        cartProjection.items.forEach { cartEntry ->
            foundProducts.put(
                cartEntry.productId,
                products.findById(cartEntry.productId)
            )
        }

        if (foundProducts.values.contains(null)) {
            throw RuntimeException("Some products failed to fetch!")
        }

        val total = cartProjection.items.map { cartEntry ->
            val productForEntry =
                foundProducts[cartEntry.productId]
                    ?: throw RuntimeException("Fetched products don't contains product with id='${cartEntry.productId.id}'")

            return@map productForEntry.price.amount * cartEntry.quantity.toBigDecimal()
        }.sumOf { it }

        priceCalculatorEventPublisher.publish(CartTotalRecalculated(command.cartId, total))
    }
}