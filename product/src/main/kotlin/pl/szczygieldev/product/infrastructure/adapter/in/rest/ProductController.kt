package pl.szczygieldev.product.infrastructure.adapter.`in`.rest

import arrow.core.raise.either
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.szczygieldev.ecommercelibrary.command.Mediator
import pl.szczygieldev.product.application.port.`in`.command.CreateProductCommand
import pl.szczygieldev.product.application.port.`in`.command.UpdateProductCommand
import pl.szczygieldev.product.application.port.out.Products
import pl.szczygieldev.product.domain.*
import pl.szczygieldev.product.domain.error.AppError
import pl.szczygieldev.product.domain.error.ProductNotFoundError
import pl.szczygieldev.product.infrastructure.adapter.`in`.rest.advice.mapToError
import pl.szczygieldev.product.infrastructure.adapter.`in`.rest.presenter.ProductPresenter
import pl.szczygieldev.product.infrastructure.adapter.`in`.rest.resource.CreateProductRequest
import pl.szczygieldev.product.infrastructure.adapter.`in`.rest.resource.ProductDto
import pl.szczygieldev.product.infrastructure.adapter.`in`.rest.resource.UpdateProductRequest
import java.util.*

@RequestMapping("/products")
@RestController
internal class ProductController(
    val mediator: Mediator,
    val products: Products,
    val productPresenter: ProductPresenter
) {
    @GetMapping
    fun getAll(
        @RequestParam(required = false) offset: Long?,
        @RequestParam(required = false) limit: Int?
    ): ResponseEntity<List<ProductDto>> {
        if (offset != null && limit != null) {
            return ResponseEntity.ok().body(products.findPage(offset, limit).map { productPresenter.toDto(it) })
        }

        return ResponseEntity.ok().body(products.findAll().map { productPresenter.toDto(it) })
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<*> {
        return either<AppError, Product> {
            val productId = ProductId(id)
            products.findById(productId) ?: raise(ProductNotFoundError(productId.id.toString()))
        }.fold({ mapToError(it) }, { product -> return ResponseEntity.ok().body(productPresenter.toDto(product)) })
    }

    @PostMapping
    suspend fun create(@RequestBody request: CreateProductRequest): ResponseEntity<*> {
        return either<AppError, Product> {
            val productId = products.nextIdentity()
            mediator.send(
                CreateProductCommand(
                    productId,
                    request.title,
                    request.description,
                    request.price,
                    ImageId(request.imageId)
                )
            )
            products.findById(productId) ?: raise(ProductNotFoundError(productId.id.toString()))
        }.fold({ mapToError(it) }, { product -> return ResponseEntity.ok().body(productPresenter.toDto(product)) })
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<*> {
        return either<AppError, Boolean> {
            val productId = ProductId(id)
            val result = products.delete(productId)

            if(!result){
                raise(ProductNotFoundError(productId.id.toString()))
            }
            result
        }.fold({ mapToError(it) }, { product -> return ResponseEntity.noContent().build<Any>() })
    }

    @PutMapping("/{id}")
    suspend fun update(@PathVariable id: UUID, @RequestBody request: UpdateProductRequest): ResponseEntity<*> {
        return either<AppError, Product> {
            val productId = ProductId(id)
            mediator.send(
                UpdateProductCommand(
                    productId,
                    ProductTitle(request.title),
                    ProductDescription(request.description),
                    ProductPrice(request.price),
                    ImageId(request.imageId)
                )
            )
            products.findById(productId) ?: raise(ProductNotFoundError(productId.id.toString()))
        }.fold({ mapToError(it) }, { product -> return ResponseEntity.ok().body(productPresenter.toDto(product)) })
    }
}
