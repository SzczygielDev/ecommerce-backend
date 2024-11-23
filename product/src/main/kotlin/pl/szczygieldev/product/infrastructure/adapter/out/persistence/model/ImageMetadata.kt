package pl.szczygieldev.product.infrastructure.adapter.out.persistence.model

import pl.szczygieldev.product.domain.ImageId
import java.time.Instant

data class ImageMetadata(
    val id: ImageId,
    val mediaType: String,
    val size: Long,
    val timestamp: Instant
)