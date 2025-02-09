package pl.szczygieldev.order.application

import arrow.core.Either
import arrow.core.mapOrAccumulate
import arrow.core.raise.either
import pl.szczygieldev.ecommercelibrary.architecture.UseCase
import pl.szczygieldev.ecommercelibrary.ddd.core.DomainEventPublisher
import pl.szczygieldev.order.application.port.`in`.PriceCalculatorUseCase
import pl.szczygieldev.order.application.port.`in`.command.CalculateCartTotalCommand
import pl.szczygieldev.order.application.port.out.Carts
import pl.szczygieldev.order.application.port.out.CartsProjections
import pl.szczygieldev.order.application.port.out.Products
import pl.szczygieldev.order.domain.PriceCalculator
import pl.szczygieldev.order.domain.error.AppError
import pl.szczygieldev.order.domain.error.CartNotFoundError
import pl.szczygieldev.order.domain.error.MissingProductForCalculateError
import pl.szczygieldev.order.domain.error.UnableToCalculateCartTotalError
import pl.szczygieldev.order.domain.event.CartTotalRecalculated
import pl.szczygieldev.order.domain.event.PriceCalculatorEvent

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