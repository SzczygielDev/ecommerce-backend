package pl.szczygieldev.ecommercebackend.domain.event

import pl.szczygieldev.ddd.core.DomainEvent
import java.time.Instant

sealed class ProductEvent : DomainEvent<ProductEvent>()

class ProductCreated : ProductEvent()