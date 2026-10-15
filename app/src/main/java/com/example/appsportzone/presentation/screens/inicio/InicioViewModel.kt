package com.example.appsportzone.presentation.screens.inicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appsportzone.domain.model.Cancha
import com.example.appsportzone.domain.usecase.GetCanchasUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InicioViewModel @Inject constructor(
    private val getCanchasUseCase: GetCanchasUseCase
) : ViewModel() {

    // Lista completa que llega del repositorio; el estado guarda solo la filtrada
    private var todasLasCanchas: List<Cancha> = emptyList()

    private val _uiState = MutableStateFlow(InicioUiState())
    val uiState = _uiState.asStateFlow()

    init {
        cargarCanchas()
    }

    fun cargarCanchas() {
        viewModelScope.launch {
            getCanchasUseCase()
                .onSuccess { canchas ->
                    todasLasCanchas = canchas
                    aplicarFiltros()
                }
                .onFailure {
                    // PENDIENTE (AWS): mostrar mensaje de error
                }
        }
    }

    fun onBusquedaChange(valor: String) {
        _uiState.update { it.copy(busqueda = valor) }
        aplicarFiltros()
    }

    fun onDisciplinaChange(disciplina: String) {
        _uiState.update { it.copy(disciplinaSeleccionada = disciplina) }
        aplicarFiltros()
    }

    private fun aplicarFiltros() {
        _uiState.update { estado ->
            estado.copy(
                canchas = todasLasCanchas.filter { cancha ->
                    cancha.disciplina == estado.disciplinaSeleccionada &&
                            (cancha.local.contains(estado.busqueda, ignoreCase = true) ||
                                    cancha.direccion.contains(estado.busqueda, ignoreCase = true))
                }
            )
        }
    }
}