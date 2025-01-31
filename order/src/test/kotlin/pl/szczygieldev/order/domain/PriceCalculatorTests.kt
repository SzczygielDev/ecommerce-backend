package pl.szczygieldev.order.domain

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.util.*

class PriceCalculatorTests : BehaviorSpec({
    val calculator = PriceCalculator()

    context("Calculate price for empty cart") {
        given("a cart with no item") {
            val cartId = CartId(UUID.randomUUID())
            val cart = Cart.create(cartId)

            `when`("price is calculated") {
                val total = calculator.calculate(cart, emptyList())

                then("total price should be 0") {
                    total.shouldBe(BigDecimal.ZERO)
                }
            }
        }
    }

    context("Calculate price for cart with multiple items") {
        given("a cart with multiple items") {
            val cartId = CartId(UUID.randomUUID())
            val cart = Cart.create(cartId)

            val products = mutableListOf<Product>()
            val productAId = ProductId(UUID.randomUUID())
            val productBId = ProductId(UUID.randomUUID())
            val productA = Product(productAId,"Product A", BigDecimal.TEN, ImageId(UUID.randomUUID()))
            val productB = Product(productBId,"Product B", BigDecimal.TEN, ImageId(UUID.randomUUID()))
            products.add(productA)
            products.add(productB)

            cart.addItem(productAId,2)
            cart.addItem(productBId,3)

            `when`("price is calculated") {
                val total = calculator.calculate(cart, products)

                then("total price should be equal to sum of products prices multiplied by their quantity") {
                    total.shouldBe(BigDecimal.valueOf(50))
                }
            }
        }
    }

    context("Calculate price for cart with multiple items without product data") {
        given("a cart with multiple items") {
            val cartId = CartId(UUID.randomUUID())
            val cart = Cart.create(cartId)

            val products = mutableListOf<Product>()
            val productAId = ProductId(UUID.randomUUID())
            val productBId = ProductId(UUID.randomUUID())
            val productA = Product(productAId,"Product A", BigDecimal.TEN, ImageId(UUID.randomUUID()))
            val productB = Product(productBId,"Product B", BigDecimal.TEN, ImageId(UUID.randomUUID()))
            products.add(productA)
            products.add(productB)

            cart.addItem(productAId,2)
            cart.addItem(productBId,3)

            `when`("price is calculated, but product data is missing") {
                val total = calculator.calculate(cart, emptyList())

                then("total price should be null") {
                    total.shouldBe(null)
                }
            }
        }
    }
})