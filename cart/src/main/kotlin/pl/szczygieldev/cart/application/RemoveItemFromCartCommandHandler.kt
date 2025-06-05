package pl.szczygieldev.cart.application

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.cart.application.port.`in`.command.RemoveItemFromCartCommand
import pl.szczygieldev.cart.application.port.out.Carts
import pl.szczygieldev.cart.domain.CartEvent
import pl.szczygieldev.cart.domain.ItemRemoveError
import pl.szczygieldev.cart.domain.ProductId
import pl.szczygieldev.ecommercelibrary.ddd.core.DomainEventPublisher

internal class RemoveItemFromCartCommandHandler(
    val carts: Carts,
    val cartEventPublisher: DomainEventPublisher<CartEvent>,
) :
    CommandWithResultHandler<RemoveItemFromCartCommand, Either<RemoveItemFromCartCommand.Error, Unit>> {
    override suspend fun handle(command: RemoveItemFromCartCommand): Either<RemoveItemFromCartCommand.Error, Unit> =
        either {
            val clientId = command.clientId

            val cart = carts.findActiveForClient(clientId) ?: raise(
                RemoveItemFromCartCommand.CartNotFoundError.forClientId(clientId)
            )
            val currentVersion = cart.version

            val result = cart.removeItem(ProductId(command.productId))
            result.onLeft { error ->
                when (error) {
                    is ItemRemoveError.CartNotActiveError -> RemoveItemFromCartCommand.CartNotActiveError.fromDomainError(
                        error
                    )
                }
            }
            val events = cart.occurredEvents()
            carts.save(cart, currentVersion)

            cartEventPublisher.publishBatch(events)
        }
}


