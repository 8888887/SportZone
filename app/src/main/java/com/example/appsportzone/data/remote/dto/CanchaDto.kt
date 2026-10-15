package com.example.appsportzone.data.remote.dto

import com.example.appsportzone.domain.model.Cancha
import com.google.gson.annotations.SerializedName

// DTO: representa una cancha TAL COMO LLEGA del API.
// @SerializedName conecta el nombre del JSON (id_cancha) con el nombre en Kotlin (idCancha).
data class CanchaDto(
    @SerializedName("id_cancha") val idCancha: String?,
    val titulo: String?,
    val descripcion: String?,
    val local: String?,
    val ubicacion: String?,
    @SerializedName("id_categoria") val idCategoria: String?,
    @SerializedName("tipo_superficie") val tipoSuperficie: String?,
    val capacidad: String?,
    val precio: String?,
    val estado: String?,
    @SerializedName("imagen_url") val imagenUrl: String?
) {
    // Convierte el DTO en el modelo que usa la app
    fun toDomain(): Cancha = Cancha(
        id = idCancha.orEmpty(),
        local = local.orEmpty().removePrefix("SportZone").trim(), // "SportZone San Miguel" -> "San Miguel"
        direccion = ubicacion.orEmpty(),
        distanciaKm = 0.0,                                         // El API no lo envía
        disciplina = disciplinaDeCategoria(idCategoria),
        tipo = titulo.orEmpty(),
        superficie = tipoSuperficie.orEmpty().replaceFirstChar { it.uppercase() },
        precioHora = precio?.toDoubleOrNull()?.toInt() ?: 0,       // Llega como texto: "70"
        calificacion = 0.0,                                        // El API no lo envía
        servicios = emptyList()                                    // El API no lo envía
    )
}

// Traduce el id de categoría al nombre que usan los filtros de la pantalla principal.
// Se usa el id y no el texto, para no depender de cómo esté escrito ("Voleibol", "Tennis"...).
private fun disciplinaDeCategoria(idCategoria: String?): String = when (idCategoria) {
    "1" -> "Fútbol"
    "2" -> "Vóley"
    "3" -> "Tenis"
    "4" -> "Natación" // No aparece en los filtros: no es parte del documento
    "5" -> "Básquet"  // Para cuando se cree esa categoría
    else -> "Otro"
}