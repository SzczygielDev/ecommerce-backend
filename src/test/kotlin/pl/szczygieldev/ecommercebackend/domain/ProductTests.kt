package pl.szczygieldev.ecommercebackend.domain

import io.kotest.core.spec.style.FunSpec
import org.junit.jupiter.api.assertThrows
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
    }
}