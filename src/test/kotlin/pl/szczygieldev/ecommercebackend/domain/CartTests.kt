package pl.szczygieldev.ecommercebackend.domain

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.assertThrows
import pl.szczygieldev.ecommercebackend.domain.error.CartAlreadySubmittedError
import pl.szczygieldev.ecommercebackend.domain.error.CartNotActiveError
import java.util.UUID

class CartTests : FunSpec({

    test("Adding item to submitted cart should fail") {
        //Arrange
        val cartId = CartId(UUID.randomUUID().toString())
        val cart = Cart.create(cartId)
        val productId = ProductId(UUID.randomUUID().toString())

        //Act
        cart.submit()
        val result = cart.addItem(productId, 1)

        //Assert
        result.isLeft().shouldBe(true)
        val error = result.leftOrNull().shouldNotBeNull()
        error.shouldBeInstanceOf<CartNotActiveError>()
    }

    test("Removing item from submitted cart should fail") {
        //Arrange
        val cartId = CartId(UUID.randomUUID().toString())
        val cart = Cart.create(cartId)
        val productId = ProductId(UUID.randomUUID().toString())

        //Act
        cart.submit()
        val result = cart.removeItem(productId)

        //Assert
        result.isLeft().shouldBe(true)
        val error = result.leftOrNull().shouldNotBeNull()
        error.shouldBeInstanceOf<CartNotActiveError>()
    }

    test("Calling submit multiple times should fail") {
        //Arrange
        val cartId = CartId(UUID.randomUUID().toString())
        val cart = Cart.create(cartId)

        //Act
        cart.submit()
        val secondSubmit = cart.submit()

        //Assert
        secondSubmit.isLeft().shouldBe(true)
        val error = secondSubmit.leftOrNull().shouldNotBeNull()
        error.shouldBeInstanceOf<CartAlreadySubmittedError>()
    }

    test("Adding negative quantity of products should throw IllegalArgumentException") {
        //Arrange
        val cartId = CartId(UUID.randomUUID().toString())
        val cart = Cart.create(cartId)
        val productId = ProductId(UUID.randomUUID().toString())

        //Act & Assert
        assertThrows<IllegalArgumentException> { cart.addItem(productId, -20) }
    }

    test("Adding zero quantity of products should throw IllegalArgumentException") {
        //Arrange
        val cartId = CartId(UUID.randomUUID().toString())
        val cart = Cart.create(cartId)
        val productId = ProductId(UUID.randomUUID().toString())

        //Act & Assert
        assertThrows<IllegalArgumentException> { cart.addItem(productId, 0) }
    }
})