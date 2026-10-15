package com.example.appsportzone.presentation.screens.reservas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appsportzone.domain.model.Reserva
import com.example.appsportzone.domain.usecase.CancelarReservaUseCase
import com.example.appsportzone.domain.usecase.GetReservasUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class MisReservasViewModel @Inject constructor(
    private val getReservasUseCase: GetReservasUseCase,
    private val cancelarReservaUseCase: CancelarReservaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MisReservasUiState())
    val uiState = _uiState.asStateFlow()

    init {
        cargarReservas()
    }

    fun cargarReservas() {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true) }
            getReservasUseCase()
                .onSuccess { lista ->
                    val ahora = LocalDateTime.now()
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            proximas = lista.filter { r -> r.esProxima(ahora) }.sortedBy { r -> r.inicio },
                            historial = lista.filterNot { r -> r.esProxima(ahora) }.sortedByDescending { r -> r.inicio }
                        )
                    }
                }
                .onFailure {
                    _uiState.update { it.copy(cargando = false) }
                }
        }
    }

    fun onPestanaChange(indice: Int) {
        _uiState.update { it.copy(pestanaSeleccionada = indice) }
    }

    fun onCancelarClick(reserva: Reserva) {
        _uiState.update { it.copy(reservaACancelar = reserva) }
    }

    fun onDescartarCancelacion() {
        _uiState.update { it.copy(reservaACancelar = null) }
    }

    fun onConfirmarCancelacion() {
        val reserva = _uiState.value.reservaACancelar ?: return
        viewModelScope.launch {
            cancelarReservaUseCase(reserva.codigo)
            _uiState.update { it.copy(reservaACancelar = null) }
            cargarReservas()
        }
    }
}