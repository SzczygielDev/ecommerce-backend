package pl.szczygieldev.order.infrastructure.adapter.out.integration.shipping.model

data class RegisterParcelRequest(val width: Double, val length: Double, val height: Double, val weight: Double)