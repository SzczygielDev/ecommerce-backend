package pl.szczygieldev.ecommercebackend.application.handlers

import arrow.core.Either
import arrow.core.raise.either
import pl.szczygieldev.ecommercebackend.application.handlers.common.CommandHandler
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.CreateCartCommand
import pl.szczygieldev.ecommercebackend.application.port.out.Carts
import pl.szczygieldev.ecommercebackend.application.port.out.CommandResultStorage
import pl.szczygieldev.ecommercebackend.domain.Cart
import pl.szczygieldev.ecommercebackend.domain.error.AppError
import pl.szczygieldev.ecommercebackend.domain.event.CartEvent
import pl.szczygieldev.shared.ddd.core.DomainEventPublisher

class CartCreateCommandHandler(
    val carts: Carts,
    val cartEventPublisher: DomainEventPublisher<CartEvent>,
    commandResultStorage: CommandResultStorage,
) :
    CommandHandler<CreateCartCommand>(commandResultStorage) {
    override suspend fun processCommand(command: CreateCartCommand): Either<AppError, Unit> = either {
        val cart = Cart.create(carts.nextIdentity())
        val version = cart.version
        val events = cart.occurredEvents()
        carts.save(cart, version)
        cartEventPublisher.publishBatch(events)
    }
}