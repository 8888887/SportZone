package com.example.appsportzone.presentation.screens.resumen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appsportzone.domain.usecase.CrearReservaUseCase
import com.example.appsportzone.domain.usecase.GetCanchaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ResumenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCanchaUseCase: GetCanchaUseCase,
    private val crearReservaUseCase: CrearReservaUseCase
) : ViewModel() {

    private val canchaId: String = savedStateHandle.get<String>("canchaId") ?: ""
    private val fechaTexto: String = savedStateHandle.get<String>("fecha") ?: ""
    private val hora: Int = savedStateHandle.get<Int>("hora") ?: 0
    private val fecha: LocalDate? = runCatching { LocalDate.parse(fechaTexto) }.getOrNull()

    private val idioma = Locale.forLanguageTag("es-PE")

    private val _uiState = MutableStateFlow(ResumenUiState())
    val uiState = _uiState.asStateFlow()

    init {
        _uiState.update {
            it.copy(textoFecha = fecha?.let { f -> formatearFecha(f) } ?: "", hora = hora)
        }
        viewModelScope.launch {
            getCanchaUseCase(canchaId)
                .onSuccess { cancha -> _uiState.update { it.copy(cancha = cancha) } }
        }
    }

    fun onMetodoPagoChange(metodo: MetodoPago) {
        _uiState.update { it.copy(metodoPago = metodo) }
    }

    fun onConfirmarClick() {
        val cancha = _uiState.value.cancha ?: return
        val fechaReserva = fecha ?: return

        viewModelScope.launch {
            crearReservaUseCase(cancha, fechaReserva, hora, _uiState.value.metodoPago.titulo)
                .onSuccess { reserva ->
                    _uiState.update { it.copy(codigoReserva = reserva.codigo, mostrarConfirmacion = true) }
                }
                .onFailure {
                    // PENDIENTE (AWS): mostrar el mensaje si el turno ya fue tomado
                }
        }
    }

    private fun formatearFecha(fecha: LocalDate): String {
        val dia = fecha.dayOfWeek.getDisplayName(TextStyle.FULL, idioma)
            .replaceFirstChar { it.uppercase() }
        val mes = fecha.month.getDisplayName(TextStyle.FULL, idioma)
        return "$dia, ${fecha.dayOfMonth} de $mes"
    }
}