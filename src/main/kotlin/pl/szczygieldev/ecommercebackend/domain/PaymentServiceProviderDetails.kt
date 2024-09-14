package pl.szczygieldev.ecommercebackend.domain

import java.net.URL

data class PaymentServiceProviderDetails(val paymentServiceProvider: PaymentServiceProvider, val displayName: String, val logoUrl : URL)
