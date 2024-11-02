package pl.szczygieldev.ecommercebackend.infrastructure.adapter.out.persistence.model

import pl.szczygieldev.ecommercebackend.domain.ImageId
import java.time.Instant

data class ImageMetadata(
    val id: ImageId,
    val mediaType: String,
    val size: Long,
    val timestamp: Instant
)