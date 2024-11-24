package pl.szczygieldev.external.shipping.dto

internal data class RegisterParcelRequest(val width: Double, val length: Double, val height: Double, val weight: Double)