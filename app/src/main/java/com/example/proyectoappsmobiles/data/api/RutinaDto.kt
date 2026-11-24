package com.example.proyectoappsmobiles.data.api

/**
 * Rutina de entrenamiento. Corresponde a la entidad `Rutina` de la API.
 */
data class RutinaDto(
    val id: Int = 0,
    val nombre: String = "",
    val tipo: String = "",
    val nivel: String = "",
    val duracionMin: Int = 0,
    val numeroEjercicios: Int = 0,
    val detalle: String = "",
    val subtitulo: String = ""
)
