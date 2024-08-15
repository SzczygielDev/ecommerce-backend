package pl.szczygieldev.ecommercebackend.domain

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.assertThrows
import pl.szczygieldev.ecommercebackend.domain.event.ProductCreated
import pl.szczygieldev.ecommercebackend.domain.event.ProductPriceUpdated
import java.math.BigDecimal
import java.util.*

class ProductTests : FunSpec() {
    init {
        test("Creating product with price equal zero should throw IllegalArgumentException") {
            //Arrange
            val productId = ProductId(UUID.randomUUID().toString())

            //Act & Assert
            assertThrows<IllegalArgumentException> {
                Product.create(
                    productId,
                    ProductTitle("title"), ProductDescription("description"), ProductPrice(BigDecimal.ZERO)
                )
            }
        }

        test("Creating product with negative price should throw IllegalArgumentException") {
            //Arrange
            val productId = ProductId(UUID.randomUUID().toString())

            //Act & Assert
            assertThrows<IllegalArgumentException> {
                Product.create(
                    productId,
                    ProductTitle("title"), ProductDescription("description"), ProductPrice(BigDecimal.valueOf(-100))
                )
            }
        }

        test("Creating product should raise ProductCreated event") {
            //Arrange
            val productId = ProductId(UUID.randomUUID().toString())

            //Act
            val product = Product.create(
                productId,
                ProductTitle("title"), ProductDescription("description"), ProductPrice(BigDecimal.TEN)
            )

            // Assert
            product.occurredEvents().filterIsInstance<ProductCreated>().shouldNotBeEmpty()
        }

        test("Applied events on product should not be present in occurredEvents of product") {
            //Arrange
            val productId = ProductId(UUID.randomUUID().toString())
            val product = Product.create(
                productId,
                ProductTitle("title"), ProductDescription("description"), ProductPrice(BigDecimal.TEN)
            )
            val priceChangeEvents = listOf(ProductPriceUpdated(productId, ProductPrice(BigDecimal.valueOf(20.0))))

            //Act
            product.applyEvents(priceChangeEvents)

            // Assert
            product.occurredEvents().shouldBeEmpty()
        }

        test("Updating product price when new price is same should not update price") {
            //Arrange
            val productId = ProductId(UUID.randomUUID().toString())
            val price = ProductPrice(BigDecimal.TEN)
            val product = Product.create(
                productId,
                ProductTitle("title"), ProductDescription("description"), price
            )

            //Act
            product.updatePrice(price)

            // Assert
            product.occurredEvents().filterIsInstance<ProductPriceUpdated>().shouldBeEmpty()
            product.priceChanges.shouldBeEmpty()
        }

        test("Updating product price when new price is different should update price") {
            //Arrange
            val productId = ProductId(UUID.randomUUID().toString())
            val product = Product.create(
                productId,
                ProductTitle("title"), ProductDescription("description"), ProductPrice(BigDecimal.TEN)
            )
            val newPrice = ProductPrice(BigDecimal.valueOf(20))

            //Act
            product.updatePrice(newPrice)

            // Assert
            product.occurredEvents().filterIsInstance<ProductPriceUpdated>().shouldNotBeEmpty()
            product.priceChanges.first().newPrice.shouldBe(newPrice)
        }
    }
}