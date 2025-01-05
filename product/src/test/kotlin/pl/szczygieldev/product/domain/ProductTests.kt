package pl.szczygieldev.product.domain

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.assertThrows
import pl.szczygieldev.product.domain.ImageId
import pl.szczygieldev.product.domain.event.ProductCreated
import pl.szczygieldev.product.domain.event.ProductPriceUpdated
import java.math.BigDecimal
import java.util.*

internal class ProductTests : FunSpec() {
    init {
        test("Creating product with price equal zero should throw IllegalArgumentException") {
            //Arrange
            val productId = ProductId(UUID.randomUUID())
            val imageId = ImageId(UUID.randomUUID())

            //Act & Assert
            assertThrows<IllegalArgumentException> {
                Product.create(
                    productId,
                    ProductTitle("title"), ProductDescription("description"), ProductPrice(BigDecimal.ZERO),imageId
                )
            }
        }

        test("Creating product with negative price should throw IllegalArgumentException") {
            //Arrange
            val productId = ProductId(UUID.randomUUID())
            val imageId = ImageId(UUID.randomUUID())

            //Act & Assert
            assertThrows<IllegalArgumentException> {
                Product.create(
                    productId,
                    ProductTitle("title"), ProductDescription("description"), ProductPrice(BigDecimal.valueOf(-100)),imageId
                )
            }
        }

        test("Creating product should raise ProductCreated event") {
            //Arrange
            val productId = ProductId(UUID.randomUUID())
            val imageId = ImageId(UUID.randomUUID())

            //Act
            val product = Product.create(
                productId,
                ProductTitle("title"), ProductDescription("description"), ProductPrice(BigDecimal.TEN),imageId
            )

            // Assert
            product.occurredEvents().filterIsInstance<ProductCreated>().shouldNotBeEmpty()
        }

        test("Applied events on product should not be present in occurredEvents of product") {
            //Arrange
            val productId = ProductId(UUID.randomUUID())
            val imageId = ImageId(UUID.randomUUID())
            val product = Product.create(
                productId,
                ProductTitle("title"), ProductDescription("description"), ProductPrice(BigDecimal.TEN),imageId
            )
            val priceChangeEvents = listOf(ProductPriceUpdated(productId, ProductPrice(BigDecimal.valueOf(20.0))))

            //Act
            product.applyEvents(priceChangeEvents)

            // Assert
            product.occurredEvents().shouldBeEmpty()
        }

        test("Updating product price when new price is same should not update price") {
            //Arrange
            val productId = ProductId(UUID.randomUUID())
            val price = ProductPrice(BigDecimal.TEN)
            val imageId = ImageId(UUID.randomUUID())
            val product = Product.create(
                productId,
                ProductTitle("title"), ProductDescription("description"), price,imageId
            )

            //Act
            product.updatePrice(price)

            // Assert
            product.occurredEvents().filterIsInstance<ProductPriceUpdated>().shouldBeEmpty()
            product.priceChanges.shouldBeEmpty()
        }

        test("Updating product price when new price is different should update price") {
            //Arrange
            val productId = ProductId(UUID.randomUUID())
            val imageId = ImageId(UUID.randomUUID())
            val product = Product.create(
                productId,
                ProductTitle("title"), ProductDescription("description"), ProductPrice(BigDecimal.TEN),imageId
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