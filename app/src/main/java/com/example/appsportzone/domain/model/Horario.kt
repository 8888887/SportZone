package com.example.appsportzone.domain.model

enum class EstadoHorario {
    DISPONIBLE,
    RESERVADO
}

data class Horario(
    val hora: Int,
    val estado: EstadoHorario
){
    val texto: String get() = formatearHora(hora)
}

fun formatearHora(hora: Int): String{
    val hora12 = if (hora % 12 == 0) 12 else hora % 12
    val sufijo = if (hora < 12) "AM" else "PM"
    return "$hora12:00 $sufijo"
}