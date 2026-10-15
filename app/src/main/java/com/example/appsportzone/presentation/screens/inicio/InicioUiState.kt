package com.example.appsportzone.presentation.screens.inicio

import com.example.appsportzone.domain.model.Cancha

data class InicioUiState(
    val sede: String = "Miraflores, Lima",
    val busqueda: String = "",
    val disciplinas: List<String> = listOf("Fútbol","Vóley","Básquet","Tenis"),
    val disciplinaSeleccionada: String = "Fútbol",
    val canchas: List<Cancha> = emptyList()
)
