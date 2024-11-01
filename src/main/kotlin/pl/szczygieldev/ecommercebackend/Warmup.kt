package pl.szczygieldev.ecommercebackend

import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaTypeFactory
import org.springframework.stereotype.Component
import pl.szczygieldev.ecommercebackend.application.port.`in`.CartUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.ProductUseCase
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.CreateCartCommand
import pl.szczygieldev.ecommercebackend.application.port.`in`.command.CreateProductCommand
import pl.szczygieldev.ecommercebackend.application.port.out.Products
import pl.szczygieldev.ecommercebackend.domain.ImageId
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.persistence.ImageRepository
import kotlin.jvm.optionals.getOrNull


@Component
class Warmup(val cartUseCase: CartUseCase, val productUseCase: ProductUseCase, val imageRepository: ImageRepository, val products: Products) {
    private fun initializeImage(path: String): ImageId? {
        val resource = ClassPathResource(
            path
        )
        val file = resource.getFile()
        val mediaType = MediaTypeFactory.getMediaType(resource).getOrNull()

        return imageRepository.uploadImage(file.inputStream(), file.length(), mediaType.toString())
    }

    @EventListener(ApplicationReadyEvent::class)
    suspend fun initData() {
        cartUseCase.createCart(CreateCartCommand())

        initializeImage("static/images/products/1.jpg")?.let {
            productUseCase.create(CreateProductCommand(products.nextIdentity(),"Produkt A", "Opis produktu", 300.0,it ))
        }
        initializeImage("static/images/products/2.jpg")?.let {
            productUseCase.create(CreateProductCommand(products.nextIdentity(),"Produkt B", "Opis produktu", 1000.0,it ))
        }
        initializeImage("static/images/products/3.jpg")?.let {
            productUseCase.create(CreateProductCommand(products.nextIdentity(),"Produkt C", "Opis produktu", 50.0,it ))
        }
        initializeImage("static/images/products/4.jpg")?.let {
            productUseCase.create(CreateProductCommand(products.nextIdentity(),"Produkt D", "Opis produktu", 100.0,it ))
        }
        initializeImage("static/images/products/5.jpg")?.let {
            productUseCase.create(CreateProductCommand(products.nextIdentity(),"Produkt E", "Opis produktu", 500.0,it ))
        }
        initializeImage("static/images/products/6.jpg")?.let {
            productUseCase.create(CreateProductCommand(products.nextIdentity(),"Produkt F", "Opis produktu", 30.0,it ))
        }
        initializeImage("static/images/products/7.jpg")?.let {
            productUseCase.create(CreateProductCommand(products.nextIdentity(),"Produkt G", "Opis produktu", 700.0,it ))
        }
        initializeImage("static/images/products/8.jpg")?.let {
            productUseCase.create(CreateProductCommand(products.nextIdentity(),"Produkt H", "Opis produktu", 30.0,it ))
        }
    }
}