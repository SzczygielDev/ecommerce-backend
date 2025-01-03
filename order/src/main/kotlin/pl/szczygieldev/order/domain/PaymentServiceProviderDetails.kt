package pl.szczygieldev.order.domain

import java.net.URL

internal data class PaymentServiceProviderDetails(val paymentServiceProvider: PaymentServiceProvider, val displayName: String, val logoUrl : URL)
