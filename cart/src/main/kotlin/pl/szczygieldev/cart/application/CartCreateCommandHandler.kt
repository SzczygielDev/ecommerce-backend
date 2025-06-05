package pl.szczygieldev.cart.application

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.cart.application.port.`in`.command.CreateCartCommand
import pl.szczygieldev.cart.application.port.out.Carts
import pl.szczygieldev.cart.domain.Cart
import pl.szczygieldev.cart.domain.CartEvent
import pl.szczygieldev.ecommercelibrary.ddd.core.DomainEventPublisher

internal class CartCreateCommandHandler(
    val carts: Carts,
    val cartEventPublisher: DomainEventPublisher<CartEvent>,
) : CommandWithResultHandler<CreateCartCommand, Either<CreateCartCommand.Error, Unit>> {
    override suspend fun handle(command: CreateCartCommand): Either<CreateCartCommand.Error, Unit> = either {
        val cart = Cart.create(carts.nextIdentity(), command.clientId)
        val version = cart.version
        val events = cart.occurredEvents()
        carts.save(cart, version)
        cartEventPublisher.publishBatch(events)
    }
}