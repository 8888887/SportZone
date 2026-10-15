package com.example.appsportzone.domain.model

data class Cancha(
    val id: String,
    val local: String,
    val direccion: String,
    val distanciaKm: Double,
    val disciplina: String,
    val tipo: String,
    val superficie: String,
    val precioHora: Int,
    val calificacion: Double,
    val servicios: List<String>
)
