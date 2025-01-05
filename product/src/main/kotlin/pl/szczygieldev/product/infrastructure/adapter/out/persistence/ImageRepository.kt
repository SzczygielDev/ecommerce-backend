package pl.szczygieldev.product.infrastructure.adapter.out.persistence

import io.github.oshai.kotlinlogging.KotlinLogging
import io.minio.*
import jakarta.annotation.PostConstruct
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository
import pl.szczygieldev.product.domain.ImageId
import pl.szczygieldev.product.infrastructure.adapter.out.persistence.table.ImageTable
import java.io.InputStream
import java.util.UUID

@Repository
internal class ImageRepository {
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

    fun uploadImage(inputStream: InputStream, size: Long, mediaType: String): ImageId? = transaction {
        val imageId = ImageId(UUID.randomUUID())

        ImageTable.insert {
            it[id] = imageId.id
            it[ImageTable.mediaType] = mediaType
            it[ImageTable.size] = size
        }

        try {
            minioClient.putObject(
                PutObjectArgs.builder()
                    .contentType(mediaType)
                    .bucket(imageBucketName)
                    .`object`(imageId.id())
                    .stream(inputStream, size, -1)
                    .build()
            )
        } catch (e: Exception) {
            log.error { e }
            return@transaction null
        }
        return@transaction  imageId
    }
}