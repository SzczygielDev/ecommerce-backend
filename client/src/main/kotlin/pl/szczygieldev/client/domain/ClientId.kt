package pl.szczygieldev.client.domain

import pl.szczygieldev.ecommercelibrary.ddd.core.UuidIdentity
import java.util.*

class ClientId(val id: UUID) : UuidIdentity<ClientId>(id)