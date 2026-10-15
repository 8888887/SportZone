package com.example.appsportzone.presentation.screens.resumen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.appsportzone.data.prueba.DatosDePrueba
import com.example.appsportzone.domain.model.formatearHora
import com.example.appsportzone.presentation.common.iconoDisciplina
import com.example.appsportzone.ui.theme.AppSportZoneTheme
import com.example.appsportzone.ui.theme.GreenDark
import java.util.Locale

// PANTALLA CONECTADA AL VIEWMODEL.
// Recibe dos acciones de navegación para los botones del diálogo de confirmación.
@Composable
fun ResumenScreen(
    onBackClick: () -> Unit = {},
    onVerReservasClick: () -> Unit = {},
    onVolverInicioClick: () -> Unit = {},
    viewModel: ResumenViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    ResumenContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onMetodoPagoChange = viewModel::onMetodoPagoChange,
        onConfirmarClick = viewModel::onConfirmarClick,
        onVerReservasClick = onVerReservasClick,
        onVolverInicioClick = onVolverInicioClick
    )
}

// PANTALLA QUE SOLO DIBUJA (se puede ver en el Preview).
@OptIn(ExperimentalMaterial3Api::class) // La barra superior de Material 3 aún es "experimental"
@Composable
fun ResumenContent(
    uiState: ResumenUiState,
    onBackClick: () -> Unit,
    onMetodoPagoChange: (MetodoPago) -> Unit,
    onConfirmarClick: () -> Unit,
    onVerReservasClick: () -> Unit,
    onVolverInicioClick: () -> Unit
) {
    val colores = MaterialTheme.colorScheme

    Scaffold(
        containerColor = colores.background,

        // Barra superior: volver, título en dos líneas y ayuda
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Confirmar Reserva",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        // Es el último paso: 1) cancha, 2) horario, 3) resumen y pago
                        Text(
                            text = "Paso 3 de 3",
                            style = MaterialTheme.typography.labelSmall,
                            color = colores.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = "Ayuda")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colores.surface)
            )
        },

        // Barra inferior fija: total y botón de confirmar
        bottomBar = { BarraPago(uiState.total, onConfirmarClick) }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { EtiquetaPaso() }

            item { TarjetaResumen(uiState) }

            item {
                SeccionPago(
                    seleccionado = uiState.metodoPago,
                    onSeleccion = onMetodoPagoChange
                )
            }

            item { NotaCancelacion() }
        }
    }

    // DIÁLOGO DE CONFIRMACIÓN: solo existe en pantalla cuando mostrarConfirmacion es true.
    // Se dibuja DESPUÉS del Scaffold para quedar encima de todo.
    if (uiState.mostrarConfirmacion) {
        DialogoConfirmacion(
            codigo = uiState.codigoReserva,
            onVerReservasClick = onVerReservasClick,
            onVolverInicioClick = onVolverInicioClick
        )
    }
}
// ---------- BLOQUES DE LA PANTALLA ----------

// Etiqueta de arriba: "Paso final: Verificación y Pago".
@Composable
private fun EtiquetaPaso() {
    val colores = MaterialTheme.colorScheme

    Surface(shape = RoundedCornerShape(50), color = colores.primaryContainer) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Verified,
                contentDescription = null,
                tint = colores.primary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "Paso final: Verificación y Pago",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = GreenDark
            )
        }
    }
}

// Tarjeta con el resumen de la reserva: deporte, sede, fecha, hora y duración.
@Composable
private fun TarjetaResumen(uiState: ResumenUiState) {
    val colores = MaterialTheme.colorScheme
    val cancha = uiState.cancha

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colores.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Resumen de tu reserva",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            // Si la cancha no se encontró, la tarjeta queda solo con el título (no se cierra la app)
            if (cancha != null) {
                FilaDato(iconoDisciplina(cancha.disciplina), "DEPORTE", cancha.tipo)
                FilaDato(Icons.Default.LocationOn, "SEDE", "SportZone · ${cancha.local}")
                FilaDato(Icons.Default.CalendarMonth, "FECHA", uiState.textoFecha)
                FilaDato(
                    Icons.Default.Schedule,
                    "HORARIO",
                    "${formatearHora(uiState.hora)} - ${formatearHora(uiState.hora + 1)}"
                )
                FilaDato(Icons.Default.Timer, "DURACIÓN", "1 hora")
            }
        }
    }
}

// Una fila del resumen: ícono a la izquierda, y a la derecha el nombre del dato y su valor.
@Composable
private fun FilaDato(icono: ImageVector, etiqueta: String, valor: String) {
    val colores = MaterialTheme.colorScheme

    Row(verticalAlignment = Alignment.CenterVertically) {
        // Círculo verde claro detrás del ícono
        Box(
            modifier = Modifier.size(38.dp).background(colores.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icono, contentDescription = null, tint = colores.primary, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = etiqueta,
                style = MaterialTheme.typography.labelSmall,
                color = colores.onSurfaceVariant
            )
            Text(
                text = valor,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
// Sección "Método de pago" con una opción por cada valor del enum MetodoPago.
@Composable
private fun SeccionPago(seleccionado: MetodoPago, onSeleccion: (MetodoPago) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Método de pago",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        // MetodoPago.entries recorre todas las opciones del enum
        MetodoPago.entries.forEach { metodo ->
            OpcionPago(
                metodo = metodo,
                seleccionado = metodo == seleccionado,
                onClick = { onSeleccion(metodo) }
            )
        }
    }
}

// Una opción de pago. La elegida lleva borde verde y fondo verde claro.
@Composable
private fun OpcionPago(metodo: MetodoPago, seleccionado: Boolean, onClick: () -> Unit) {
    val colores = MaterialTheme.colorScheme

    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = if (seleccionado) colores.primaryContainer else colores.surface,
        border = BorderStroke(
            width = if (seleccionado) 2.dp else 1.dp,
            color = if (seleccionado) colores.primary else colores.outline
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (metodo == MetodoPago.YAPE) Icons.Default.PhoneAndroid else Icons.Default.CreditCard,
                contentDescription = null,
                tint = colores.primary
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = metodo.titulo,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = metodo.detalle,
                    style = MaterialTheme.typography.labelSmall,
                    color = colores.onSurfaceVariant
                )
            }
            // RadioButton es el circulito de selección. onClick = null porque toda la fila ya es tocable.
            RadioButton(selected = seleccionado, onClick = null)
        }
    }
}

// Aviso sobre cancelación (texto del diseño).
@Composable
private fun NotaCancelacion() {
    val colores = MaterialTheme.colorScheme

    Surface(shape = RoundedCornerShape(12.dp), color = colores.primaryContainer) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                tint = GreenDark,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Cancelación gratuita hasta 3 horas antes del encuentro.",
                style = MaterialTheme.typography.bodySmall,
                color = GreenDark
            )
        }
    }
}

// Barra inferior fija: total a pagar y botón de confirmar.
@Composable
private fun BarraPago(total: Int, onConfirmarClick: () -> Unit) {
    val colores = MaterialTheme.colorScheme

    Surface(color = colores.surface, shadowElevation = 8.dp) {
        // navigationBarsPadding() evita que el botón quede tapado por los botones del celular
        Row(
            modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Total a pagar",
                    style = MaterialTheme.typography.labelMedium,
                    color = colores.onSurfaceVariant
                )
                // "%.2f" muestra siempre 2 decimales: 80 -> "80.00"
                Text(
                    text = "S/ " + "%.2f".format(Locale.US, total.toDouble()),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colores.primary
                )
            }
            Spacer(Modifier.width(16.dp))
            Button(
                onClick = onConfirmarClick,
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Confirmar Reserva", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Default.CheckCircle, contentDescription = null)
            }
        }
    }
}

// DIÁLOGO "¡Reserva Confirmada!": ícono de check, mensaje, código y dos botones.
// onDismissRequest = {} impide cerrarlo tocando fuera: el usuario debe elegir uno de los dos botones.
@Composable
private fun DialogoConfirmacion(
    codigo: String,
    onVerReservasClick: () -> Unit,
    onVolverInicioClick: () -> Unit
) {
    val colores = MaterialTheme.colorScheme

    AlertDialog(
        onDismissRequest = {},
        containerColor = colores.surface,
        shape = RoundedCornerShape(24.dp),
        // Círculo verde con el check
        icon = {
            Box(
                modifier = Modifier.size(64.dp).background(colores.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = colores.primary,
                    modifier = Modifier.size(36.dp)
                )
            }
        },
        title = {
            Text(
                text = "¡Reserva Confirmada!",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Tu cancha ya está separada. Te enviaremos el comprobante por correo.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colores.onSurfaceVariant
                )
                Spacer(Modifier.height(16.dp))
                // Caja con el código de reserva
                Surface(shape = RoundedCornerShape(12.dp), color = colores.primaryContainer) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "CÓDIGO DE RESERVA",
                            style = MaterialTheme.typography.labelSmall,
                            color = colores.onSurfaceVariant
                        )
                        Text(
                            text = codigo,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = GreenDark
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onVerReservasClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Ver mis reservas")
            }
        },
        dismissButton = {
            TextButton(onClick = onVolverInicioClick, modifier = Modifier.fillMaxWidth()) {
                Text("Volver al inicio")
            }
        }
    )
}

// VISTA PREVIA con datos armados a mano. Cambia mostrarConfirmacion a true para ver el diálogo.
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ResumenPreview() {
    AppSportZoneTheme {
        ResumenContent(
            uiState = ResumenUiState(
                cancha = DatosDePrueba.canchas[0],
                textoFecha = "Viernes, 16 de octubre",
                hora = 19,
                mostrarConfirmacion = false,
                codigoReserva = "SZ-LIMA-84920"
            ),
            onBackClick = {},
            onMetodoPagoChange = {},
            onConfirmarClick = {},
            onVerReservasClick = {},
            onVolverInicioClick = {}
        )
    }
}