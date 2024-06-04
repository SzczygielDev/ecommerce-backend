package pl.szczygieldev.ecommercebackend.application

import arrow.core.Either
import arrow.core.mapOrAccumulate
import arrow.core.raise.either
import pl.szczygieldev.shared.ddd.core.DomainEventPublisher
import pl.szczygieldev.ecommercebackend.application.port.`in`.PriceCalculatorUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.CalculateCartTotalCommand
import pl.szczygieldev.ecommercebackend.application.port.out.CartsProjections
import pl.szczygieldev.ecommercebackend.application.port.out.Products
import pl.szczygieldev.ecommercebackend.domain.error.*
import pl.szczygieldev.ecommercebackend.domain.event.CartTotalRecalculated
import pl.szczygieldev.ecommercebackend.domain.event.PriceCalculatorEvent
import pl.szczygieldev.shared.architecture.UseCase

@UseCase
class PriceCalculatorService(
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

            return@mapOrAccumulate product.price.amount * cartEntry.quantity.toBigDecimal()
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