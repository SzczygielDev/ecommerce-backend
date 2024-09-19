package pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.persistence

import io.github.oshai.kotlinlogging.KotlinLogging
import io.minio.*
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Repository
import pl.szczygieldev.ecommercebackend.domain.ImageId
import java.io.InputStream
import java.util.UUID

@Repository
class ImageRepository {
    companion object {
        private val log = KotlinLogging.logger { }
    }

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

    fun uploadImage(input: InputStream, size: Long): ImageId? {
        val imageId = ImageId(UUID.randomUUID().toString())
        try {
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(imageBucketName)
                    .`object`(imageId.id)
                    .stream(input, size, -1)
                    .build()
            )
        } catch (e: Exception) {
            log.error { e }
            return null
        }
        return imageId;
    }

    fun getImage(id: ImageId): ByteArray? {
        try {
            val response =
                minioClient.getObject(GetObjectArgs.builder().bucket(imageBucketName).`object`(id.id).build())
                    ?: return null
            return response.readAllBytes()
        } catch (e: Exception) {
            log.error { "Error while fetching image id='${id.id}' error='$e'" }
            return null
        }
    }
}