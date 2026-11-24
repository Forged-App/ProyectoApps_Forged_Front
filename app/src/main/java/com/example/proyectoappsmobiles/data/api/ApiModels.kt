package com.example.proyectoappsmobiles.data.api

/**
 * Cuerpos de petición y respuestas auxiliares para la API Forged.
 * Los nombres de los campos coinciden con los DTO del backend Spring Boot.
 */

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegistroRequest(
    val nombre: String,
    val email: String,
    val password: String
)

data class UsuarioDto(
    val email: String,
    val nombre: String
)

data class RecuperarResponse(
    val existe: Boolean,
    val mensaje: String? = null
)

data class ReservaRequest(
    val idClase: Int?,
    val titulo: String,
    val fecha: String,
    val hora: String,
    val entrenador: String,
    val duracion: String,
    val esGrupal: Boolean,
    val emailUsuario: String? = null
)

data class MembresiaRequest(
    val tipoPlan: String,
    val duracion: String,
    val costo: String,
    val fechaInicio: String,
    val fechaVencimiento: String,
    val metodoPago: String,
    val emailUsuario: String? = null
)
