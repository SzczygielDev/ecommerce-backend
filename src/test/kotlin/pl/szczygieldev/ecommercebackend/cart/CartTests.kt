package pl.szczygieldev.ecommercebackend.cart

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import pl.szczygieldev.ecommercebackend.domain.Cart
import pl.szczygieldev.ecommercebackend.domain.CartId
import pl.szczygieldev.ecommercebackend.domain.ProductId
import pl.szczygieldev.ecommercebackend.domain.error.CartAlreadySubmittedError
import pl.szczygieldev.ecommercebackend.domain.error.CartNotActiveError
import java.util.UUID

class CartTests : FunSpec({

    test("Adding item to submitted cart should fail"){
        val cartId = CartId(UUID.randomUUID().toString())
        val cart =Cart.create(cartId)

        cart.submit()

        val productId = ProductId(UUID.randomUUID().toString())
        val result = cart.addItem(productId,1)

        result.isLeft().shouldBe(true)
        val error = result.leftOrNull().shouldNotBeNull()
        error.shouldBeInstanceOf<CartNotActiveError>()
    }

    test("Removing item from submitted cart should fail"){
        val cartId = CartId(UUID.randomUUID().toString())
        val cart =Cart.create(cartId)

        cart.submit()

        val productId = ProductId(UUID.randomUUID().toString())
        val result = cart.removeItem(productId)

        result.isLeft().shouldBe(true)
        val error = result.leftOrNull().shouldNotBeNull()
        error.shouldBeInstanceOf<CartNotActiveError>()
    }

    test("Calling submit multiple times should fail"){
        val cartId = CartId(UUID.randomUUID().toString())
        val cart =Cart.create(cartId)

        cart.submit()
        val secondSubmit = cart.submit()

        secondSubmit.isLeft().shouldBe(true)
        val error = secondSubmit.leftOrNull().shouldNotBeNull()
        error.shouldBeInstanceOf<CartAlreadySubmittedError>()
    }
})