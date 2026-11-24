package com.example.proyectoappsmobiles.data.api

/**
 * Ejercicio que pertenece a una rutina.
 * Corresponde a la entidad `EjercicioRutina` de la API.
 */
data class EjercicioRutinaDto(
    val id: Int = 0,
    val idRutina: Int = 0,
    val nombre: String = "",
    val tiempo: String = "",
    val completado: Boolean = false
)
