package com.example.proyectoappsmobiles.data.api

/**
 * Pago registrado en la API. Corresponde a la entidad `Pago`.
 */
data class PagoDto(
    val id: Int = 0,
    val plan: String = "",
    val monto: String = "",
    val fechaInicio: String = "",
    val fechaVencimiento: String = "",
    val metodoPago: String = "",
    val estado: String = "",
    val emailUsuario: String? = null
)

/**
 * Cuerpo para registrar un pago (POST /pagos).
 */
data class PagoRequest(
    val plan: String,
    val monto: String,
    val fechaInicio: String,
    val fechaVencimiento: String,
    val metodoPago: String,
    val estado: String = "Pagado",
    val emailUsuario: String? = null
)
