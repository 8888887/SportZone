package com.example.appsportzone.presentation.screens.reservas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.appsportzone.data.prueba.DatosDePrueba
import com.example.appsportzone.domain.model.Cancha
import com.example.appsportzone.domain.model.EstadoReserva
import com.example.appsportzone.domain.model.Reserva
import com.example.appsportzone.domain.model.formatearHora
import com.example.appsportzone.presentation.common.AppBottomBar
import com.example.appsportzone.presentation.common.iconoDisciplina
import com.example.appsportzone.ui.theme.AppSportZoneTheme
import com.example.appsportzone.ui.theme.GreenDark
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

// PANTALLA CONECTADA AL VIEWMODEL
@Composable
fun MisReservasScreen(
    onBackClick: () -> Unit = {},
    onBottomBarClick: (String) -> Unit = {},
    onVolverAReservar: (canchaId: String) -> Unit = {},
    viewModel: MisReservasViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    MisReservasContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onBottomBarClick = onBottomBarClick,
        onPestanaChange = viewModel::onPestanaChange,
        onCancelarClick = viewModel::onCancelarClick,
        onConfirmarCancelacion = viewModel::onConfirmarCancelacion,
        onDescartarCancelacion = viewModel::onDescartarCancelacion,
        onVolverAReservar = onVolverAReservar
    )
}

// PANTALLA QUE SOLO DIBUJA
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisReservasContent(
    uiState: MisReservasUiState,
    onBackClick: () -> Unit,
    onBottomBarClick: (String) -> Unit,
    onPestanaChange: (Int) -> Unit,
    onCancelarClick: (Reserva) -> Unit,
    onConfirmarCancelacion: () -> Unit,
    onDescartarCancelacion: () -> Unit,
    onVolverAReservar: (String) -> Unit
) {
    val colores = MaterialTheme.colorScheme

    Scaffold(
        containerColor = colores.background,
        topBar = {
            TopAppBar(
                title = {
                    Text("Mis Reservas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = {}) { Icon(Icons.Default.Search, contentDescription = "Buscar") }
                    IconButton(onClick = {}) { Icon(Icons.Default.Notifications, contentDescription = "Notificaciones") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colores.surface)
            )
        },
        bottomBar = { AppBottomBar(rutaActual = "reservas", onItemClick = onBottomBarClick) }
    ) { innerPadding ->

        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Pestanas(uiState, onPestanaChange)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when {
                    uiState.cargando -> item { Cargando() }

                    // Pestaña "Próximas"
                    uiState.pestanaSeleccionada == 0 -> {
                        item { NotaCancelacion() }
                        item { TituloSeccion("Próximos partidos") }
                        if (uiState.proximas.isEmpty()) {
                            item { SinReservas("No tienes reservas próximas") }
                        }
                        items(uiState.proximas, key = { it.codigo }) { reserva ->
                            TarjetaProxima(reserva, onCancelarClick = { onCancelarClick(reserva) })
                        }
                    }

                    // Pestaña "Historial"
                    else -> {
                        item { TituloSeccion("Historial reciente") }
                        if (uiState.historial.isEmpty()) {
                            item { SinReservas("Aún no tienes reservas anteriores") }
                        }
                        items(uiState.historial, key = { it.codigo }) { reserva ->
                            TarjetaHistorial(reserva, onVolverAReservar = { onVolverAReservar(reserva.cancha.id) })
                        }
                    }
                }
            }
        }
    }

    // El diálogo existe solo mientras haya una reserva por cancelar
    uiState.reservaACancelar?.let { reserva ->
        DialogoCancelar(reserva, onConfirmarCancelacion, onDescartarCancelacion)
    }
}

// ---------- BLOQUES ----------

@Composable
private fun Pestanas(uiState: MisReservasUiState, onPestanaChange: (Int) -> Unit) {
    val colores = MaterialTheme.colorScheme
    val titulos = listOf(
        "Próximas (${uiState.proximas.size})",
        "Historial (${uiState.historial.size})"
    )

    TabRow(
        selectedTabIndex = uiState.pestanaSeleccionada,
        containerColor = colores.surface,
        contentColor = colores.primary
    ) {
        titulos.forEachIndexed { indice, titulo ->
            Tab(
                selected = indice == uiState.pestanaSeleccionada,
                onClick = { onPestanaChange(indice) },
                text = { Text(titulo, fontWeight = FontWeight.SemiBold) },
                selectedContentColor = colores.primary,
                unselectedContentColor = colores.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun NotaCancelacion() {
    Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.primaryContainer) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Info, contentDescription = null, tint = GreenDark, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Puedes cancelar tu reserva sin penalidad hasta ${Reserva.HORAS_CANCELACION} horas antes del inicio del turno.",
                style = MaterialTheme.typography.bodySmall,
                color = GreenDark
            )
        }
    }
}

@Composable
private fun TituloSeccion(texto: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(texto, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Text("Lima, Perú", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun Cargando() {
    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun SinReservas(texto: String) {
    Text(
        text = texto,
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

// Tarjeta de una reserva próxima
@Composable
private fun TarjetaProxima(reserva: Reserva, onCancelarClick: () -> Unit) {
    val colores = MaterialTheme.colorScheme
    val cancha = reserva.cancha
    val puedeCancelar = reserva.sePuedeCancelar()
    val limite = reserva.limiteCancelacion

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colores.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ChipDisciplina(cancha)
                ChipEstado("Confirmada", colores.primaryContainer, GreenDark)
            }

            Column {
                Text("SportZone ${cancha.local}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    "${cancha.tipo} · ${cancha.superficie}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colores.onSurfaceVariant
                )
            }

            // Caja con fecha y horario
            Surface(shape = RoundedCornerShape(12.dp), color = colores.primaryContainer.copy(alpha = 0.4f)) {
                Row(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                    DatoCaja(Icons.Default.CalendarMonth, "Fecha", textoDia(reserva.fecha), Modifier.weight(1f))
                    DatoCaja(Icons.Default.Schedule, "Horario", textoRango(reserva.hora), Modifier.weight(1f))
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = colores.onSurfaceVariant, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text(
                    cancha.direccion,
                    style = MaterialTheme.typography.bodySmall,
                    color = colores.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    "Total: ${textoSoles(reserva.total)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = colores.primary
                )
            }

            OutlinedButton(
                onClick = onCancelarClick,
                enabled = puedeCancelar, // Fuera de plazo, el botón se desactiva
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, if (puedeCancelar) colores.error else colores.outline),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = colores.error)
            ) {
                Icon(Icons.Default.EventBusy, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Cancelar reserva")
            }

            Text(
                text = if (puedeCancelar)
                    "Cancelación gratuita hasta ${textoDia(limite.toLocalDate())}, ${formatearHora(limite.hour)}"
                else
                    "Ya pasó el plazo de cancelación gratuita",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                color = colores.onSurfaceVariant
            )
        }
    }
}

// Tarjeta del historial: finalizada o cancelada
@Composable
private fun TarjetaHistorial(reserva: Reserva, onVolverAReservar: () -> Unit) {
    val colores = MaterialTheme.colorScheme
    val cancelada = reserva.estado == EstadoReserva.CANCELADA

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colores.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ChipDisciplina(reserva.cancha)
                if (cancelada) {
                    ChipEstado("Cancelada", colores.error.copy(alpha = 0.12f), colores.error)
                } else {
                    ChipEstado("Finalizada", colores.outline.copy(alpha = 0.25f), colores.onSurfaceVariant)
                }
            }

            Text(
                "SportZone ${reserva.cancha.local}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                textDecoration = if (cancelada) TextDecoration.LineThrough else null
            )
            Text(
                "${textoFechaCompleta(reserva.fecha)} • ${textoRango(reserva.hora)}",
                style = MaterialTheme.typography.bodySmall,
                color = colores.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    if (cancelada) "Cancelada sin cargo" else "Pagado: ${textoSoles(reserva.total)}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
                TextButton(onClick = onVolverAReservar) {
                    Icon(Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Volver a reservar")
                }
            }
        }
    }
}

@Composable
private fun ChipDisciplina(cancha: Cancha) {
    Surface(shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.primaryContainer) {
        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(iconoDisciplina(cancha.disciplina), contentDescription = null, tint = GreenDark, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            Text(cancha.tipo, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, color = GreenDark)
        }
    }
}

@Composable
private fun ChipEstado(texto: String, fondo: Color, colorTexto: Color) {
    Surface(shape = RoundedCornerShape(50), color = fondo) {
        Text(
            texto,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = colorTexto
        )
    }
}

@Composable
private fun DatoCaja(icono: ImageVector, etiqueta: String, valor: String, modifier: Modifier = Modifier) {
    val colores = MaterialTheme.colorScheme
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(icono, contentDescription = null, tint = colores.primary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Column {
            Text(etiqueta, style = MaterialTheme.typography.labelSmall, color = colores.onSurfaceVariant)
            Text(valor, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun DialogoCancelar(reserva: Reserva, onConfirmar: () -> Unit, onDescartar: () -> Unit) {
    val colores = MaterialTheme.colorScheme
    AlertDialog(
        onDismissRequest = onDescartar,
        containerColor = colores.surface,
        icon = { Icon(Icons.Default.EventBusy, contentDescription = null, tint = colores.error) },
        title = { Text("¿Cancelar reserva?", fontWeight = FontWeight.Bold) },
        text = {
            Text(
                "Se liberará tu turno del ${textoDia(reserva.fecha)}, ${textoRango(reserva.hora)} " +
                        "en SportZone ${reserva.cancha.local}. Esta acción no se puede deshacer."
            )
        },
        confirmButton = {
            Button(onClick = onConfirmar, colors = ButtonDefaults.buttonColors(containerColor = colores.error)) {
                Text("Sí, cancelar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDescartar) { Text("No, mantener") }
        }
    )
}

// ---------- FORMATOS DE TEXTO ----------

private val idioma: Locale = Locale.forLanguageTag("es-PE")

// "Hoy, 14 oct", "Mañana, 15 oct" o "Sáb, 18 oct"
private fun textoDia(fecha: LocalDate): String {
    val hoy = LocalDate.now()
    val dia = when (fecha) {
        hoy -> "Hoy"
        hoy.plusDays(1) -> "Mañana"
        else -> fecha.dayOfWeek.getDisplayName(TextStyle.SHORT, idioma).replaceFirstChar { it.uppercase() }
    }
    return "$dia, ${fecha.dayOfMonth} ${fecha.month.getDisplayName(TextStyle.SHORT, idioma)}"
}

// "10 oct 2026"
private fun textoFechaCompleta(fecha: LocalDate): String =
    "${fecha.dayOfMonth} ${fecha.month.getDisplayName(TextStyle.SHORT, idioma)} ${fecha.year}"

// "7:00 PM - 8:00 PM"
private fun textoRango(hora: Int): String = "${formatearHora(hora)} - ${formatearHora(hora + 1)}"

// "S/ 80.00"
private fun textoSoles(monto: Int): String = "S/ " + "%.2f".format(Locale.US, monto.toDouble())

// ---------- PREVIEW ----------

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MisReservasPreview() {
    val hoy = LocalDate.now()
    AppSportZoneTheme {
        MisReservasContent(
            uiState = MisReservasUiState(
                cargando = false,
                proximas = listOf(
                    Reserva("SZ-LIMA-84920", DatosDePrueba.canchas[0], hoy.plusDays(1), 19, 80, "Tarjeta", EstadoReserva.CONFIRMADA),
                    Reserva("SZ-LIMA-51377", DatosDePrueba.canchas[3], hoy.plusDays(3), 18, 90, "Yape", EstadoReserva.CONFIRMADA)
                )
            ),
            onBackClick = {},
            onBottomBarClick = {},
            onPestanaChange = {},
            onCancelarClick = {},
            onConfirmarCancelacion = {},
            onDescartarCancelacion = {},
            onVolverAReservar = {}
        )
    }
}