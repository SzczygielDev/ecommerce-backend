package pl.szczygieldev.product.infrastructure.adapter.`in`.rest

import arrow.core.raise.either
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.szczygieldev.product.application.port.out.Products
import pl.szczygieldev.product.domain.Product
import pl.szczygieldev.product.domain.ProductId
import pl.szczygieldev.product.domain.error.AppError
import pl.szczygieldev.product.domain.error.ProductNotFoundError
import pl.szczygieldev.product.infrastructure.adapter.`in`.rest.advice.mapToError
import pl.szczygieldev.product.infrastructure.adapter.`in`.rest.presenter.ProductPresenter
import pl.szczygieldev.product.infrastructure.adapter.`in`.rest.resource.ProductFullDto
import java.util.*


@RequestMapping("/admin/products")
@RestController
internal class AdminProductController(
    val products: Products,
    val productPresenter: ProductPresenter
) {
    @GetMapping
    fun getAll(
        @RequestParam(required = false) offset: Long?,
        @RequestParam(required = false) limit: Int?
    ): ResponseEntity<List<ProductFullDto>> {
        if (offset != null && limit != null) {
            return ResponseEntity.ok().body(products.findPage(offset, limit).map { productPresenter.toFullDto(it) })
        }

        return ResponseEntity.ok().body(products.findAll().map { productPresenter.toFullDto(it) })
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<*> {
        return either<AppError, Product> {
            val productId = ProductId(id)
            products.findById(productId) ?: raise(ProductNotFoundError(productId.id.toString()))
        }.fold({ mapToError(it) }, { product -> return ResponseEntity.ok().body(productPresenter.toFullDto(product)) })
    }
}