package pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest

import arrow.core.raise.either
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.MediaType
import org.springframework.http.MediaTypeFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import pl.szczygieldev.ecommercebackend.domain.ImageId
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.error.ImageUploadError
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.advice.mapToError
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.`in`.rest.resource.ImageUploadResponse
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.persistence.ImageRepository
import kotlin.jvm.optionals.getOrNull

@RestController
@RequestMapping("/images")
class ImageController(val imageRepository: ImageRepository) {

    @GetMapping("/{imageId}", produces = [MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE])
    fun getImage(@PathVariable imageId: String): ResponseEntity<ByteArrayResource> {
        val id = ImageId(imageId)
        val bytes = imageRepository.getImage(id) ?: return ResponseEntity.notFound().build()

       return ResponseEntity.ok(ByteArrayResource(bytes))
    }

    @PostMapping
    fun uploadImage(@RequestParam("file") file: MultipartFile): ResponseEntity<*> {
        return either {
            val mediaType = MediaTypeFactory.getMediaType(file.resource).getOrNull()
                ?: raise(ImageUploadError.mediaTypeNotFound())
            if (mediaType != MediaType.IMAGE_JPEG && mediaType != MediaType.IMAGE_PNG){
                raise(ImageUploadError.mediaTypeNotSupported())
            }
            val id = imageRepository.uploadImage(
                file.inputStream,
                file.size
            ) ?: raise(ImageUploadError.storageError())
            id.id()
        }.fold<ResponseEntity<*>>(
            { mapToError(it) },
            { ResponseEntity.ok(ImageUploadResponse(it)) })
    }
}