package pl.szczygieldev.cart.application

import arrow.core.Either
import arrow.core.mapOrAccumulate
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.cart.application.port.`in`.command.CalculateCartTotalCommand
import pl.szczygieldev.cart.application.port.out.Carts
import pl.szczygieldev.cart.application.port.out.Products
import pl.szczygieldev.cart.domain.CartTotalRecalculated
import pl.szczygieldev.cart.domain.PriceCalculator
import pl.szczygieldev.cart.domain.PriceCalculatorEvent
import pl.szczygieldev.ecommercelibrary.ddd.core.DomainEventPublisher

internal class CalculateCartTotalCommandHandler(
    val priceCalculator: PriceCalculator,
    val products: Products,
    val carts: Carts,
    val priceCalculatorEventPublisher: DomainEventPublisher<PriceCalculatorEvent>,
) :
    CommandWithResultHandler<CalculateCartTotalCommand, Either<CalculateCartTotalCommand.Error,Unit>> {
    override suspend fun handle(command: CalculateCartTotalCommand): Either<CalculateCartTotalCommand.Error, Unit> = either{
        val cartId = command.cartId
        val cart = carts.findById(cartId) ?: raise(CalculateCartTotalCommand.CartNotFoundError.forId(cartId))

        val total = cart.items.mapOrAccumulate { cartEntry ->
            val product = products.findById(cartEntry.productId)
                ?: raise(CalculateCartTotalCommand.MissingProductForCalculateError.forProduct(cartEntry.productId))

            return@mapOrAccumulate product
        }.fold({ errors ->
            val ids = errors.map { error -> error.productId.id() }.toList().toString()
            raise(CalculateCartTotalCommand.UnableToCalculateCartTotalError("Failed to fetch products with ids='$ids'"))
        }, { products ->
            priceCalculator.calculate(cart, products)
                ?: raise(CalculateCartTotalCommand.UnableToCalculateCartTotalError("Failed to calculate cart"))
        })

        priceCalculatorEventPublisher.publish(CartTotalRecalculated(command.cartId, total))
    }
}

