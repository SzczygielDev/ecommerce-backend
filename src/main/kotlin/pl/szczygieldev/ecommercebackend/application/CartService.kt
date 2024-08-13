package pl.szczygieldev.ecommercebackend.application

import arrow.core.Either
import arrow.core.raise.either
import pl.szczygieldev.ecommercebackend.application.handlers.common.CommandHandler
import pl.szczygieldev.shared.ddd.core.DomainEventPublisher
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.AddItemToCartCommand
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.RemoveItemFromCartCommand
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.SubmitCartCommand
import pl.szczygieldev.ecommercebackend.application.port.`in`.CartUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.CreateCartCommand
import pl.szczygieldev.ecommercebackend.application.port.out.Carts
import pl.szczygieldev.ecommercebackend.application.port.out.Products
import pl.szczygieldev.ecommercebackend.domain.Cart
import pl.szczygieldev.ecommercebackend.domain.CartId
import pl.szczygieldev.ecommercebackend.domain.ProductId
import pl.szczygieldev.ecommercebackend.domain.error.AppError
import pl.szczygieldev.ecommercebackend.domain.error.CartNotFoundError
import pl.szczygieldev.ecommercebackend.domain.error.ProductNotFoundError
import pl.szczygieldev.ecommercebackend.domain.event.CartEvent
import pl.szczygieldev.shared.architecture.UseCase

@UseCase
class CartService(
    val carts: Carts,
    val products: Products,
    val cartEventPublisher: DomainEventPublisher<CartEvent>,
    val cartCreateCommandHandler: CommandHandler<CreateCartCommand>
) : CartUseCase {

    override suspend fun createCart(command: CreateCartCommand): Either<AppError, Unit> = either {
        cartCreateCommandHandler.execute(command).bind()
    }

    override fun submitCart(command: SubmitCartCommand): Either<AppError, Unit> = either {
        val cartId = CartId(command.cartId)
        val cart = carts.findById(cartId) ?: raise(CartNotFoundError.forId(cartId))
        val currentVersion = cart.version

        cart.submit(command.deliveryProvider, command.paymentServiceProvider).bind()

        val events = cart.occurredEvents()
        carts.save(cart, currentVersion)

        cartEventPublisher.publishBatch(events)
    }

    override fun addProductToCart(command: AddItemToCartCommand): Either<AppError, Unit> = either {
        val cartId = CartId(command.cartId)
        val cart = carts.findById(cartId) ?: raise(CartNotFoundError.forId(cartId))
        val currentVersion = cart.version

        val productId = ProductId(command.productId)
        val product = products.findById(productId) ?: raise(ProductNotFoundError.forId(productId))

        cart.addItem(product.productId, command.quantity).bind()

        val events = cart.occurredEvents()
        carts.save(cart, currentVersion)

        cartEventPublisher.publishBatch(events)
    }

    override fun removeProductFromCart(command: RemoveItemFromCartCommand): Either<AppError, Unit> = either {
        val cartId = CartId(command.cartId)
        val cart = carts.findById(cartId) ?: raise(CartNotFoundError.forId(cartId))
        val currentVersion = cart.version

        cart.removeItem(ProductId(command.productId)).bind()

        val events = cart.occurredEvents()
        carts.save(cart, currentVersion)

        cartEventPublisher.publishBatch(events)
    }
}