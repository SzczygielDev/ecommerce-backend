package pl.szczygieldev.cart.application

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.cart.application.port.`in`.command.AddItemToCartCommand
import pl.szczygieldev.cart.application.port.out.Carts
import pl.szczygieldev.cart.application.port.out.Products
import pl.szczygieldev.cart.domain.AddToCartError
import pl.szczygieldev.cart.domain.CartEvent
import pl.szczygieldev.cart.domain.ProductId
import pl.szczygieldev.ecommercelibrary.ddd.core.DomainEventPublisher

internal class AddItemToCartCommandHandler(
    val carts: Carts,
    val products: Products,
    val cartEventPublisher: DomainEventPublisher<CartEvent>,
) :
    CommandWithResultHandler<AddItemToCartCommand, Either<AddItemToCartCommand.Error, Unit>> {
    override suspend fun handle(command: AddItemToCartCommand): Either<AddItemToCartCommand.Error, Unit> = either {
        val clientId = command.clientId

        val cart =
            carts.findActiveForClient(clientId) ?: raise(AddItemToCartCommand.CartNotFoundError.forClientId(clientId))

        val currentVersion = cart.version

        val productId = ProductId(command.productId.id)
        val product = products.findById(productId) ?: raise(AddItemToCartCommand.ProductNotFoundError())

        val result = cart.addItem(product.productId, command.quantity)
        result.onLeft { error ->
            when (error) {
                is AddToCartError.CartNotActiveError -> AddItemToCartCommand.CartNotActiveError.fromDomainError(error)
            }
        }
        val events = cart.occurredEvents()
        carts.save(cart, currentVersion)

        cartEventPublisher.publishBatch(events)
    }
}
