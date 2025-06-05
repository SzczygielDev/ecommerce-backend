package pl.szczygieldev.product.infrastructure.adapter.`in`.http

import org.springframework.http.MediaType
import org.springframework.http.MediaTypeFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import pl.szczygieldev.product.infrastructure.adapter.`in`.http.error.ImageUploadError
import pl.szczygieldev.product.infrastructure.adapter.`in`.http.resource.ImageUploadResponse
import pl.szczygieldev.product.infrastructure.adapter.out.persistence.ImageRepository
import kotlin.jvm.optionals.getOrNull

@RestController
@RequestMapping("/images")
internal class ImageController(val imageRepository: ImageRepository) {

    @PostMapping
    fun uploadImage(@RequestParam("file") file: MultipartFile): ResponseEntity<*> {
        val mediaType = MediaTypeFactory.getMediaType(file.resource).getOrNull()
            ?: return ImageUploadError.mediaTypeNotFound()
        if (mediaType != MediaType.IMAGE_JPEG && mediaType != MediaType.IMAGE_PNG) {
            return ImageUploadError.mediaTypeNotSupported()
        }

        val mediaTypeValue = when (mediaType) {
            MediaType.IMAGE_JPEG -> {
                MediaType.IMAGE_JPEG_VALUE
            }
            MediaType.IMAGE_PNG -> {
                MediaType.IMAGE_PNG_VALUE
            }
            else -> {
                return ImageUploadError.mediaTypeNotSupported()
            }
        }

        val uploadDetails = imageRepository.uploadImage(
            file.inputStream, file.size, mediaTypeValue
        ) ?: return ImageUploadError.storageError()

        return ResponseEntity.ok(ImageUploadResponse(uploadDetails.id()))
    }
}