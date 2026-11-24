package com.example.proyectoappsmobiles.data.api

/**
 * Franja de disponibilidad de un entrenador para un día concreto.
 * Corresponde a la entidad `Disponibilidad` de la API.
 */
data class DisponibilidadDto(
    val id: Int = 0,
    val idEntrenador: Int = 0,
    val entrenador: String = "",
    val dia: String = "",
    val hora: String = "",
    val disponible: Boolean = true
)
