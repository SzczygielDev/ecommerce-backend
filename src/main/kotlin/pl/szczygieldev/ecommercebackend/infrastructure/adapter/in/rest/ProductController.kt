package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.szczygieldev.ecommercebackend.application.port.`in`.ProductUseCase
import pl.szczygieldev.ecommercebackend.application.port.out.Products
import pl.szczygieldev.ecommercebackend.domain.ProductId

@RequestMapping("/products")
@RestController
class ProductController(val productUseCase: ProductUseCase,val products: Products) {
    @GetMapping
    fun getAll(): ResponseEntity<*> = ResponseEntity.ok().body(products.findAll())

}