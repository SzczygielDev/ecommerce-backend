package pl.szczygieldev.cart.application

import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.shouldForAtLeastOne
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.*
import pl.szczygieldev.cart.application.port.`in`.CartUseCase
import pl.szczygieldev.cart.application.port.`in`.command.AddItemToCartCommand
import pl.szczygieldev.cart.application.port.`in`.command.CreateCartCommand
import pl.szczygieldev.cart.application.port.`in`.command.RemoveItemFromCartCommand
import pl.szczygieldev.cart.application.port.`in`.command.SubmitCartCommand
import pl.szczygieldev.cart.application.port.out.Carts
import pl.szczygieldev.cart.application.port.out.Products
import pl.szczygieldev.cart.domain.*
import pl.szczygieldev.ecommercelibrary.ddd.core.DomainEventPublisher
import java.math.BigDecimal
import java.util.*

internal class CartUseCaseTests : FunSpec() {
    val productsMock = mockk<Products>()
    val cartsMock = mockk<Carts>()
    val eventPublisherMock = mockk<DomainEventPublisher<CartEvent>>()
    val cartUseCase: CartUseCase = CartService(
        cartsMock,
        productsMock,
        eventPublisherMock,
    )

    init {
        every { cartsMock.save(any(), any()) } just runs
        every { eventPublisherMock.publish(any()) } just runs
        every { eventPublisherMock.publishBatch(any()) } just runs


        test("Submitting cart should raise CartNotFoundError when cart not found") {
            //Arrange
            val cartId = CartId(UUID.randomUUID())
            val paymentServiceProvider = PaymentServiceProvider.MOCK_PSP
            val deliveryProvider = DeliveryProvider.MOCK_DELIVERY_PROVIDER

            every { cartsMock.findById(cartId) } returns null

            //Act
            val result =
                cartUseCase.submitCart(SubmitCartCommand(cartId.idAsUUID(), deliveryProvider, paymentServiceProvider))

            //Assert
            result.isLeft().shouldBe(true)
            val error = result.leftOrNull().shouldNotBeNull()
            error.shouldBeInstanceOf<CartNotFoundError>()
        }
        test("Submitting cart should call submit on it when cart found") {
            //Arrange
            val cartId = CartId(UUID.randomUUID())
            val cart = spyk<Cart>(Cart.create(cartId))
            every { cartsMock.nextIdentity() } returns CartId(UUID.randomUUID())
            every { cartsMock.findById(cartId) } returns cart
            val paymentServiceProvider = PaymentServiceProvider.MOCK_PSP
            val deliveryProvider = DeliveryProvider.MOCK_DELIVERY_PROVIDER

            //Act
            cartUseCase.submitCart(SubmitCartCommand(cartId.idAsUUID(), deliveryProvider, paymentServiceProvider))

            //Assert
            verify { cart.submit(deliveryProvider, paymentServiceProvider) }
        }

        test("Submitting cart should create new cart") {
            //Arrange
            val cartId = CartId(UUID.randomUUID())
            val cart = Cart.create(cartId)
            val newCartId = CartId(UUID.randomUUID())
            val paymentServiceProvider = PaymentServiceProvider.MOCK_PSP
            val deliveryProvider = DeliveryProvider.MOCK_DELIVERY_PROVIDER

            val savedCartsSlot = mutableListOf<Cart>()

            every { cartsMock.nextIdentity() } returns newCartId
            every { cartsMock.findById(cartId) } returns cart
            every { cartsMock.save(capture(savedCartsSlot), any()) } just runs

            //Act
            cartUseCase.submitCart(SubmitCartCommand(cartId.idAsUUID(), deliveryProvider, paymentServiceProvider))

            //Assert
            savedCartsSlot.size.shouldBe(2)
            savedCartsSlot.shouldForAtLeastOne { cart -> cart.cartId.sameValueAs(newCartId) }
        }

        test("Submitting cart should publish events from current and new cart") {
            //Arrange
            val cartId = CartId(UUID.randomUUID())
            val cart = Cart.create(cartId)
            val newCartId = CartId(UUID.randomUUID())
            val paymentServiceProvider = PaymentServiceProvider.MOCK_PSP
            val deliveryProvider = DeliveryProvider.MOCK_DELIVERY_PROVIDER

            val events = mutableListOf<List<CartEvent>>()

            every { cartsMock.nextIdentity() } returns newCartId
            every { cartsMock.findById(cartId) } returns cart
            every { cartsMock.save(any(), any()) } just runs
            every { eventPublisherMock.publishBatch(capture(events)) } just runs
            //Act
            cartUseCase.submitCart(SubmitCartCommand(cartId.idAsUUID(), deliveryProvider, paymentServiceProvider))

            //Assert
            val publishedEvents = events.first()
            publishedEvents.shouldForAtLeastOne { event -> event is CartCreated  && event.cartId.sameValueAs(newCartId) }
            publishedEvents.shouldForAtLeastOne { event -> event is CartSubmitted && event.cartId.sameValueAs(cartId) }
        }

        test("Adding product to cart should raise CartNotFoundError when cart not found") {
            //Arrange
            val cartId = CartId(UUID.randomUUID())
            val productId = ProductId(UUID.randomUUID())
            every { cartsMock.findById(cartId) } returns null

            //Act
            val result = cartUseCase.addProductToCart(AddItemToCartCommand(cartId.idAsUUID(), productId.idAsUUID(), 1))

            //Assert
            result.isLeft().shouldBe(true)
            val error = result.leftOrNull().shouldNotBeNull()
            error.shouldBeInstanceOf<CartNotFoundError>()
        }

        test("Adding product to cart should raise ProductNotFoundError when product not found") {
            //Arrange
            val cartId = CartId(UUID.randomUUID())
            val productId = ProductId(UUID.randomUUID())
            val cart = Cart.create(cartId)
            every { cartsMock.findById(cartId) } returns cart
            every { productsMock.findById(productId) } returns null

            //Act
            val result = cartUseCase.addProductToCart(AddItemToCartCommand(cartId.idAsUUID(), productId.idAsUUID(), 1))

            //Assert
            result.isLeft().shouldBe(true)
            val error = result.leftOrNull().shouldNotBeNull()
            error.shouldBeInstanceOf<ProductNotFoundError>()
        }

        test("Adding product to cart should call addItem with found product") {
            //Arrange
            val cartId = CartId(UUID.randomUUID())
            val cart = spyk<Cart>(Cart.create(cartId))
            val productId = ProductId(UUID.randomUUID())
            val product = Product(
                productId,
                "product A",
                BigDecimal.valueOf(250),
            )
            every { cartsMock.findById(cartId) } returns cart
            every { productsMock.findById(productId) } returns product
            val command = AddItemToCartCommand(cartId.idAsUUID(), productId.idAsUUID(), 1)

            //Act
            val result = cartUseCase.addProductToCart(command)

            //Assert
            verify { cart.addItem(productId, 1) }
        }

        test("Removing product from cart should raise CartNotFoundError when cart not found") {
            //Arrange
            val cartId = CartId(UUID.randomUUID())
            val productId = ProductId(UUID.randomUUID())
            every { cartsMock.findById(cartId) } returns null

            //Act
            val result = cartUseCase.removeProductFromCart(RemoveItemFromCartCommand(cartId.idAsUUID(), productId.idAsUUID()))

            //Assert
            result.isLeft().shouldBe(true)
            val error = result.leftOrNull().shouldNotBeNull()
            error.shouldBeInstanceOf<CartNotFoundError>()
        }

        test("Removing product from cart should call removeItem on it when cart found") {
            //Arrange
            val cartId = CartId(UUID.randomUUID())
            val cart = spyk<Cart>(Cart.create(cartId))
            val productId = ProductId(UUID.randomUUID())
            val product = Product(
                productId,
                "product A",
                BigDecimal.valueOf(250),

            )

            every { cartsMock.findById(cartId) } returns cart
            every { productsMock.findById(productId) } returns product
            val command = RemoveItemFromCartCommand(cartId.idAsUUID(), productId.idAsUUID())

            //Act
            val result = cartUseCase.removeProductFromCart(command)

            //Assert
            verify { cart.removeItem(productId) }
        }

        test("Cart should be saved when no error occurred") {
            //Arrange
            val command = CreateCartCommand()
            val cartId = CartId(UUID.randomUUID())
            every { cartsMock.nextIdentity() } returns cartId

            //Act
            cartUseCase.createCart(command)

            //Assert
            verify { cartsMock.save(any(), any()) }
        }

        test("Cart occurred events should be published when no error occurred"){
            //Arrange
            val command = CreateCartCommand()
            val cartId = CartId(UUID.randomUUID())
            every { cartsMock.nextIdentity() } returns cartId

            //Act
            cartUseCase.createCart(command)

            //Assert
            verify { eventPublisherMock.publishBatch(any()) }
        }
    }
}