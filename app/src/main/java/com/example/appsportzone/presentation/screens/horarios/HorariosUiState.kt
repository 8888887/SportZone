package com.example.appsportzone.presentation.screens.horarios

import com.example.appsportzone.domain.model.Cancha
import com.example.appsportzone.domain.model.Horario
import java.time.LocalDate

data class OpcionFecha(
    val fecha: LocalDate,
    val titulo: String,
    val subtitulo: String
)

data class HorariosUiState(
    val cancha: Cancha? = null,
    val fechas: List<OpcionFecha> = emptyList(),
    val fechaSeleccionada: LocalDate? = null,
    val mesAnio: String = "",
    val horarios: List<Horario> = emptyList(),
    val horaSeleccionada: Int? = null
){
    val total: Int
        get() = if (horaSeleccionada != null) (cancha?.precioHora ?: 0) else 0
}
