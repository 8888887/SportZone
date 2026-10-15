package com.example.appsportzone.presentation.screens.horarios

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appsportzone.domain.usecase.GetCanchaUseCase
import com.example.appsportzone.domain.usecase.GetHorariosUseCase
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
class HorariosViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle, // Hilt lo entrega solo, con el canchaId de la ruta
    private val getCanchaUseCase: GetCanchaUseCase,
    private val getHorariosUseCase: GetHorariosUseCase
) : ViewModel() {

    private val canchaId: String = savedStateHandle.get<String>("canchaId") ?: ""
    private val idioma = Locale.forLanguageTag("es-PE")

    private val _uiState = MutableStateFlow(HorariosUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val hoy = LocalDate.now()
        val fechas = (0..2).map { dias -> crearOpcion(hoy.plusDays(dias.toLong()), dias) }
        val mes = hoy.month.getDisplayName(TextStyle.FULL, idioma).replaceFirstChar { it.uppercase() }

        _uiState.update { it.copy(fechas = fechas, mesAnio = "$mes ${hoy.year}") }

        viewModelScope.launch {
            getCanchaUseCase(canchaId)
                .onSuccess { cancha -> _uiState.update { it.copy(cancha = cancha) } }
        }

        onFechaChange(hoy)
    }

    fun onFechaChange(fecha: LocalDate) {
        _uiState.update {
            it.copy(fechaSeleccionada = fecha, horaSeleccionada = null, horarios = emptyList())
        }
        viewModelScope.launch {
            getHorariosUseCase(canchaId, fecha)
                .onSuccess { lista ->
                    // Solo aplicamos la respuesta si el usuario no cambió de fecha mientras cargaba
                    _uiState.update { if (it.fechaSeleccionada == fecha) it.copy(horarios = lista) else it }
                }
        }
    }

    fun onHoraChange(hora: Int) {
        _uiState.update {
            it.copy(horaSeleccionada = if (it.horaSeleccionada == hora) null else hora)
        }
    }

    private fun crearOpcion(fecha: LocalDate, diasDesdeHoy: Int): OpcionFecha {
        val titulo = when (diasDesdeHoy) {
            0 -> "Hoy"
            1 -> "Mañana"
            else -> fecha.dayOfWeek.getDisplayName(TextStyle.SHORT, idioma)
                .replaceFirstChar { it.uppercase() }
        }
        val mesCorto = fecha.month.getDisplayName(TextStyle.SHORT, idioma)
        return OpcionFecha(fecha, titulo, "${fecha.dayOfMonth} $mesCorto")
    }
}