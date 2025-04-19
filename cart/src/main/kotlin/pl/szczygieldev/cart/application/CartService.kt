package pl.szczygieldev.cart.application

import arrow.core.Either
import arrow.core.raise.either
import pl.szczygieldev.cart.application.port.`in`.CartUseCase
import pl.szczygieldev.cart.application.port.`in`.command.AddItemToCartCommand
import pl.szczygieldev.cart.application.port.`in`.command.CreateCartCommand
import pl.szczygieldev.cart.application.port.`in`.command.RemoveItemFromCartCommand
import pl.szczygieldev.cart.application.port.`in`.command.SubmitCartCommand
import pl.szczygieldev.cart.application.port.out.Carts
import pl.szczygieldev.cart.application.port.out.Products
import pl.szczygieldev.cart.domain.*
import pl.szczygieldev.cart.domain.Cart
import pl.szczygieldev.cart.domain.CartEvent
import pl.szczygieldev.ecommercelibrary.architecture.UseCase
import pl.szczygieldev.ecommercelibrary.ddd.core.DomainEventPublisher


@UseCase
internal class CartService(
    val carts: Carts,
    val products: Products,
    val cartEventPublisher: DomainEventPublisher<CartEvent>,

    ) : CartUseCase {


    override suspend fun createCart(command: CreateCartCommand): Either<CreateCartCommand.Error, Unit> =
        either {
            val cart = Cart.create(carts.nextIdentity(),command.clientId)
            val version = cart.version
            val events = cart.occurredEvents()
            carts.save(cart, version)
            cartEventPublisher.publishBatch(events)
        }

    override fun submitCart(command: SubmitCartCommand): Either<SubmitCartCommand.Error, Unit> = either {
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

        val newCart = Cart.create(carts.nextIdentity(),clientId)
        val newCartVersion = newCart.version

        val events = cart.occurredEvents() + newCart.occurredEvents()

        carts.save(cart, currentVersion)
        carts.save(newCart, newCartVersion)
        cartEventPublisher.publishBatch(events)
    }

    override fun addProductToCart(command: AddItemToCartCommand): Either<AddItemToCartCommand.Error, Unit> = either {
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

    override fun removeProductFromCart(command: RemoveItemFromCartCommand): Either<RemoveItemFromCartCommand.Error, Unit> =
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