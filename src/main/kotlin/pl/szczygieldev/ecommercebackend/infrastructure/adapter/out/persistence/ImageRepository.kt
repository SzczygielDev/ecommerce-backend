package pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.persistence

import io.github.oshai.kotlinlogging.KotlinLogging
import io.minio.*
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Repository
import org.springframework.web.multipart.MultipartFile
import pl.szczygieldev.ecommercebackend.domain.ImageId
import pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.persistence.model.ImageMetadata
import java.time.Instant
import java.util.UUID

@Repository
class ImageRepository {
    companion object {
        private val log = KotlinLogging.logger { }
    }

    private val db = mutableMapOf<String, ImageMetadata>()

    private val storageUrl = "http://localhost:8333"
    private val imageBucketName = "images"
    private val minioClient = MinioClient.builder()
        .endpoint(storageUrl)
        .build()

    @PostConstruct
    private fun initializeBuckets() {
        log.info { "Initializing buckets" }
        try {
            val found = minioClient.bucketExists(
                BucketExistsArgs.builder()
                    .bucket(imageBucketName)
                    .build()
            )

            if (!found) {
                log.info { "$imageBucketName bucket not found, creating" }
                minioClient.makeBucket(
                    MakeBucketArgs.builder()
                        .bucket(imageBucketName)
                        .build()
                )
            }
        } catch (e: Exception) {
            log.error { "Error while initializing buckets $e" }
        }
    }

    fun uploadImage(file: MultipartFile, mediaType: String): ImageId? {
        val imageId = ImageId(UUID.randomUUID().toString())
        db[imageId.id()] = ImageMetadata(imageId, mediaType, file.size, Instant.now())

        try {
            minioClient.putObject(
                PutObjectArgs.builder()
                    .contentType(mediaType)
                    .bucket(imageBucketName)
                    .`object`(imageId.id())
                    .stream(file.inputStream, file.size, -1)
                    .build()
            )
        } catch (e: Exception) {
            log.error { e }
            return null
        }
        return imageId
    }
}