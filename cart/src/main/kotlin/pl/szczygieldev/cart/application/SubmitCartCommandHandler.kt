package pl.szczygieldev.cart.application

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.cart.application.port.`in`.command.SubmitCartCommand
import pl.szczygieldev.cart.application.port.out.Carts
import pl.szczygieldev.cart.domain.Cart
import pl.szczygieldev.cart.domain.CartEvent
import pl.szczygieldev.cart.domain.SubmitError
import pl.szczygieldev.ecommercelibrary.ddd.core.DomainEventPublisher

internal class SubmitCartCommandHandler(val carts: Carts, val cartEventPublisher: DomainEventPublisher<CartEvent>) :
    CommandWithResultHandler<SubmitCartCommand, Either<SubmitCartCommand.Error, Unit>> {
    override suspend fun handle(command: SubmitCartCommand): Either<SubmitCartCommand.Error, Unit> = either {
        val clientId = command.clientId

        val cart = carts.findActiveForClient(clientId) ?: raise(SubmitCartCommand.CartNotFoundError.ClientId(clientId))
        val currentVersion = cart.version

        val result = cart.submit(command.deliveryProvider, command.paymentServiceProvider)

        result.onLeft { error ->
            val mappedError = when (error) {
                is SubmitError.CartAlreadySubmittedError -> SubmitCartCommand.CartAlreadySubmittedError.fromDomainError(
                    error
                )
            }
            raise(mappedError)
        }

        val newCart = Cart.create(carts.nextIdentity(), clientId)
        val newCartVersion = newCart.version

        val events = cart.occurredEvents() + newCart.occurredEvents()

        carts.save(cart, currentVersion)
        carts.save(newCart, newCartVersion)
        cartEventPublisher.publishBatch(events)
    }
}