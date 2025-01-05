package pl.szczygieldev.product.domain

import pl.szczygieldev.ecommercelibrary.ddd.core.UuidIdentity
import java.util.*

internal data class ImageId(val id: UUID) : UuidIdentity<ImageId>(id)