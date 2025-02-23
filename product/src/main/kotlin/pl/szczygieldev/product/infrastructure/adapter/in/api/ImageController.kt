package pl.szczygieldev.product.infrastructure.adapter.`in`.api

import arrow.core.raise.either
import org.springframework.http.MediaType
import org.springframework.http.MediaTypeFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import pl.szczygieldev.product.infrastructure.adapter.error.ImageUploadError
import pl.szczygieldev.product.infrastructure.adapter.`in`.api.advice.mapToError
import pl.szczygieldev.product.infrastructure.adapter.`in`.api.resource.ImageUploadResponse
import pl.szczygieldev.product.infrastructure.adapter.out.persistence.ImageRepository
import kotlin.jvm.optionals.getOrNull

@RestController
@RequestMapping("/images")
internal class ImageController(val imageRepository: ImageRepository) {

    @PostMapping
    fun uploadImage(@RequestParam("file") file: MultipartFile): ResponseEntity<*> {
        return either {
            val mediaType = MediaTypeFactory.getMediaType(file.resource).getOrNull()
                ?: raise(ImageUploadError.mediaTypeNotFound())
            if (mediaType != MediaType.IMAGE_JPEG && mediaType != MediaType.IMAGE_PNG) {
                raise(ImageUploadError.mediaTypeNotSupported())
            }

            val mediaTypeValue = when (mediaType) {
                MediaType.IMAGE_JPEG -> {
                    MediaType.IMAGE_JPEG_VALUE
                }
                MediaType.IMAGE_PNG -> {
                    MediaType.IMAGE_PNG_VALUE
                }
                else -> {
                    raise(ImageUploadError.mediaTypeNotSupported())
                }
            }

            val uploadDetails = imageRepository.uploadImage(
                file.inputStream, file.size, mediaTypeValue
            ) ?: raise(ImageUploadError.storageError())

            uploadDetails
        }.fold<ResponseEntity<*>>(
            { mapToError(it) },
            { ResponseEntity.ok(ImageUploadResponse(it.id())) })
    }
}