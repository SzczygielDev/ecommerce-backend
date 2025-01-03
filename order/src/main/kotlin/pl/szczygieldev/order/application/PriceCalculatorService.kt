package pl.szczygieldev.order.application

import arrow.core.Either
import arrow.core.mapOrAccumulate
import arrow.core.raise.either
import pl.szczygieldev.ecommercelibrary.architecture.UseCase
import pl.szczygieldev.ecommercelibrary.ddd.core.DomainEventPublisher
import pl.szczygieldev.order.application.port.`in`.PriceCalculatorUseCase
import pl.szczygieldev.order.application.port.`in`.command.CalculateCartTotalCommand
import pl.szczygieldev.order.application.port.out.CartsProjections
import pl.szczygieldev.order.application.port.out.Products
import pl.szczygieldev.order.domain.error.AppError
import pl.szczygieldev.order.domain.error.CartNotFoundError
import pl.szczygieldev.order.domain.error.MissingProductForCalculateError
import pl.szczygieldev.order.domain.error.UnableToCalculateCartTotalError
import pl.szczygieldev.order.domain.event.CartTotalRecalculated
import pl.szczygieldev.order.domain.event.PriceCalculatorEvent
@UseCase
internal class PriceCalculatorService(
    val products: Products,
    val cartProjections: CartsProjections,
    val priceCalculatorEventPublisher: DomainEventPublisher<PriceCalculatorEvent>,
) : PriceCalculatorUseCase {
    override fun calculateCartTotal(command: CalculateCartTotalCommand): Either<AppError, Unit> = either {
        val cartId = command.cartId
        val cartProjection = cartProjections.findById(cartId) ?: raise(CartNotFoundError.forId(cartId))

        val total = cartProjection.items.mapOrAccumulate { cartEntry ->
            val productId = cartEntry.productId
            val product = products.findById(productId)
                ?: raise(MissingProductForCalculateError.forProduct(productId))

            return@mapOrAccumulate product.price * cartEntry.quantity.toBigDecimal()
        }.fold({
            errors ->
            val ids = errors.map { error -> error.productId.id() }.toList().toString()
            raise(UnableToCalculateCartTotalError("Failed to fetch products with ids='$ids'"))
        }, {
            it.sumOf { value -> value }
        })
        priceCalculatorEventPublisher.publish(CartTotalRecalculated(command.cartId, total))
    }
}