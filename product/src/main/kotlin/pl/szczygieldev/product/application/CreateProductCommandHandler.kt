package pl.szczygieldev.product.application

import arrow.core.Either
import arrow.core.raise.either
import com.trendyol.kediatr.CommandWithResultHandler
import pl.szczygieldev.product.application.port.`in`.command.CreateProductCommand
import pl.szczygieldev.product.application.port.out.Products
import pl.szczygieldev.product.domain.Product
import pl.szczygieldev.product.domain.ProductDescription
import pl.szczygieldev.product.domain.ProductPrice
import pl.szczygieldev.product.domain.ProductTitle
import java.math.BigDecimal

internal class CreateProductCommandHandler(val products: Products) :
    CommandWithResultHandler<CreateProductCommand, Either<CreateProductCommand.Error, Unit>> {

    override suspend fun handle(command: CreateProductCommand): Either<CreateProductCommand.Error, Unit> = either {
        val product = Product.create(
            command.productId,
            ProductTitle(command.title),
            ProductDescription(command.description),
            ProductPrice(BigDecimal.valueOf(command.price)),
            command.imageId
        )

        products.save(product, product.version)
    }
}

