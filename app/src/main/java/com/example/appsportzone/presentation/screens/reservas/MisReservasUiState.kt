package com.example.appsportzone.presentation.screens.reservas

import com.example.appsportzone.domain.model.Reserva

data class MisReservasUiState(
    val cargando: Boolean = true,
    val pestanaSeleccionada: Int = 0,               // 0 = Próximas, 1 = Historial
    val proximas: List<Reserva> = emptyList(),
    val historial: List<Reserva> = emptyList(),
    val reservaACancelar: Reserva? = null           // Si no es null, se muestra el diálogo de cancelar
)
