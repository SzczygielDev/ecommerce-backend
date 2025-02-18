package pl.szczygieldev.cart.application

import arrow.core.Either
import arrow.core.mapOrAccumulate
import arrow.core.raise.either
import pl.szczygieldev.cart.application.port.`in`.PriceCalculatorUseCase
import pl.szczygieldev.cart.application.port.`in`.command.CalculateCartTotalCommand
import pl.szczygieldev.cart.application.port.out.Carts
import pl.szczygieldev.cart.application.port.out.Products
import pl.szczygieldev.cart.domain.*
import pl.szczygieldev.cart.domain.AppError
import pl.szczygieldev.cart.domain.CartNotFoundError
import pl.szczygieldev.cart.domain.CartTotalRecalculated
import pl.szczygieldev.cart.domain.PriceCalculator
import pl.szczygieldev.cart.domain.PriceCalculatorEvent
import pl.szczygieldev.ecommercelibrary.architecture.UseCase
import pl.szczygieldev.ecommercelibrary.ddd.core.DomainEventPublisher

@UseCase
internal class PriceCalculatorService(
    val priceCalculator: PriceCalculator,
    val products: Products,
    val carts: Carts,
    val priceCalculatorEventPublisher: DomainEventPublisher<PriceCalculatorEvent>,
) : PriceCalculatorUseCase {
    override fun calculateCartTotal(command: CalculateCartTotalCommand): Either<AppError, Unit> = either {
        val cartId = command.cartId
        val cart = carts.findById(cartId) ?: raise(CartNotFoundError.forId(cartId))

        val total = cart.items.mapOrAccumulate { cartEntry ->
            val product = products.findById(cartEntry.productId)
                ?: raise(MissingProductForCalculateError.forProduct(cartEntry.productId))

            return@mapOrAccumulate product
        }.fold({ errors ->
            val ids = errors.map { error -> error.productId.id() }.toList().toString()
            raise(UnableToCalculateCartTotalError("Failed to fetch products with ids='$ids'"))
        }, { products ->
            priceCalculator.calculate(cart, products) ?: raise(UnableToCalculateCartTotalError("Failed to calculate cart"))
        })

        priceCalculatorEventPublisher.publish(CartTotalRecalculated(command.cartId, total))
    }
}