package pl.szczygieldev.external.shipping.model

data class RegisterParcelRequest(val width: Double, val length: Double, val height: Double, val weight: Double)