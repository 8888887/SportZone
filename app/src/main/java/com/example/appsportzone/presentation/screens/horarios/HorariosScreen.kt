package com.example.appsportzone.presentation.screens.horarios

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.appsportzone.data.prueba.DatosDePrueba
import com.example.appsportzone.domain.model.Cancha
import com.example.appsportzone.domain.model.EstadoHorario
import com.example.appsportzone.domain.model.Horario
import com.example.appsportzone.domain.model.formatearHora
import com.example.appsportzone.presentation.common.iconoDisciplina
import com.example.appsportzone.ui.theme.AppSportZoneTheme
import com.example.appsportzone.ui.theme.GreenDark
import java.time.LocalDate

//Pantalla conectada al viewModel al igual que InicioScreen
@Composable
fun HorariosScreen(
    onBackClick: () -> Unit = {},
    // el id de la cancha, la fecha como texto (2026-10-16) y la hora (19).
    onContinuarClick: (canchaId: String, fecha: String, hora: Int) -> Unit = { _, _, _ -> },
    viewModel: HorariosViewModel = hiltViewModel()
){
    val uiState by viewModel.uiState.collectAsState()

    HorariosContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onFechaChange = viewModel::onFechaChange,
        onHoraChange = viewModel::onHoraChange,
        onContinuarClick = {
            val cancha = uiState.cancha
            val fecha = uiState.fechaSeleccionada
            val hora = uiState.horaSeleccionada
            if (cancha != null && fecha != null && hora != null) {
                onContinuarClick(cancha.id, fecha.toString(), hora)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HorariosContent(
    uiState: HorariosUiState,
    onBackClick: () -> Unit,
    onFechaChange: (LocalDate) -> Unit,
    onHoraChange: (Int) -> Unit,
    onContinuarClick: () -> Unit
){
    val colores = MaterialTheme.colorScheme

    Scaffold(
        containerColor = colores.background,
        // Barra superior: flecha para volver, título en dos líneas e ícono de información
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Seleccionar Horario",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        // Es el paso 2 de 3: 1) elegir cancha, 2) elegir horario, 3) resumen y pago
                        Text(
                            text = "Paso 2 de 3",
                            style = MaterialTheme.typography.labelSmall,
                            color = colores.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "volver")
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Info, contentDescription = "Información")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colores.surface)
            )
        },
        // Barra inferior fija: resumen de lo elegido y botón para continuar
        bottomBar = { BarraResumen(uiState, onContinuarClick) }
    ) {
        innerPaddingg ->

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPaddingg),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            uiState.cancha?.let { cancha ->
                item { ResumenCancha(cancha) }
            }
            item {
                SeccionFecha(
                    mesAnio = uiState.mesAnio,
                    fechas = uiState.fechas,
                    seleccionada = uiState.fechaSeleccionada,
                    onFechaChange = onFechaChange
                )
            }
            item { Leyenda() }

            item { TituloHorarios() }

            item {
                GrillaHorarios(
                    horarios = uiState.horarios,
                    horaSeleccionada = uiState.horaSeleccionada,
                    precio = uiState.cancha?.precioHora ?: 0,
                    onHoraChange = onHoraChange
                )
            }
            item { NotaCancelacion() }
        }
    }
}

//------------------BLOQUES DE PANTALLA------------------------
//Tarjeta con los datos de la cancha elegida
@Composable
private fun ResumenCancha(cancha: Cancha) {
    val colores = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colores.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .background(
                        Brush.verticalGradient(listOf(colores.primary, GreenDark)),
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconoDisciplina(cancha.disciplina),
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cancha.tipo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = colores.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(2.dp))
                    Text(
                        text = "Sede ${cancha.local}",
                        style = MaterialTheme.typography.bodySmall,
                        color = colores.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "S/ ${cancha.precioHora}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colores.primary
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "por hora",
                        style = MaterialTheme.typography.bodySmall,
                        color = colores.onSurfaceVariant
                    )
                }
            }

            // Etiqueta con la superficie (por ejemplo "Techada")
            Surface(shape = RoundedCornerShape(8.dp), color = colores.primaryContainer) {
                Text(
                    text = cancha.superficie,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = GreenDark
                )
            }
        }
    }
}

// Título "Fecha de partido" y los chips de día.
@Composable
private fun SeccionFecha(
    mesAnio: String,
    fechas: List<OpcionFecha>,
    seleccionada: LocalDate?,
    onFechaChange: (LocalDate) -> Unit
) {
    val colores = MaterialTheme.colorScheme

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = colores.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Fecha de partido",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = mesAnio,
                style = MaterialTheme.typography.labelMedium,
                color = colores.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(12.dp))

        // Se coloca el weight(1f) para que los 3 ocupen el mismo ancho
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            fechas.forEach { opcion ->
                ChipFecha(
                    opcion = opcion,
                    seleccionada = opcion.fecha == seleccionada,
                    onClick = { onFechaChange(opcion.fecha) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// Chip de fecha. Seleccionado: fondo verde y un check. No seleccionado: fondo blanco con borde.
@Composable
private fun ChipFecha(
    opcion: OpcionFecha,
    seleccionada: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colores = MaterialTheme.colorScheme
    val colorTexto = if (seleccionada) Color.White else colores.onSurface

    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = if (seleccionada) colores.primary else colores.surface,
        border = if (seleccionada) null else BorderStroke(1.dp, colores.outline)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (seleccionada) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                }
                Text(
                    text = opcion.titulo,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = colorTexto
                )
            }
            Text(
                text = opcion.subtitulo,
                style = MaterialTheme.typography.labelSmall,
                color = colorTexto.copy(alpha = 0.8f)
            )
        }
    }
}

// Leyenda de colores: Disponible, Seleccionado y Reservado.
@Composable
private fun Leyenda() {
    val colores = MaterialTheme.colorScheme

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ItemLeyenda(colores.primaryContainer, "Disponible")
        ItemLeyenda(colores.primary, "Seleccionado")
        ItemLeyenda(colores.outline, "Reservado")
    }
}

// Un punto de color con su texto.
@Composable
private fun ItemLeyenda(color: Color, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(12.dp).background(color, CircleShape))
        Spacer(Modifier.width(6.dp))
        Text(
            text = texto,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Título de la sección de horarios y la duración de cada turno.
@Composable
private fun TituloHorarios() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Horarios: día y noche",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Duración: 60 min",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Cuadrícula de turnos en 2 columnas.
@Composable
private fun GrillaHorarios(
    horarios: List<Horario>,
    horaSeleccionada: Int?,
    precio: Int,
    onHoraChange: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        horarios.chunked(2).forEach { fila ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                fila.forEach { horario ->
                    TarjetaHorario(
                        horario = horario,
                        seleccionado = horario.hora == horaSeleccionada,
                        precio = precio,
                        onClick = { onHoraChange(horario.hora) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Si la última fila tiene un solo turno, dejamos un hueco para que no se estire
                if (fila.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

// Un turno. Tiene 3 aspectos según su estado: disponible, seleccionado o reservado.
@Composable
private fun TarjetaHorario(
    horario: Horario,
    seleccionado: Boolean,
    precio: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colores = MaterialTheme.colorScheme
    val reservado = horario.estado == EstadoHorario.RESERVADO

    // Cada "when" elige el valor según el estado, de arriba hacia abajo
    val fondo = when {
        seleccionado -> colores.primary
        reservado -> colores.outline.copy(alpha = 0.25f)
        else -> colores.surface
    }
    val colorHora = when {
        seleccionado -> Color.White
        reservado -> colores.onSurfaceVariant
        else -> colores.onSurface
    }
    val textoEstado = when {
        seleccionado -> "Tu selección"
        reservado -> "Reservado"
        else -> "Disponible"
    }
    val colorEstado = when {
        seleccionado -> Color.White.copy(alpha = 0.85f)
        reservado -> colores.onSurfaceVariant
        else -> colores.primary
    }
    // Solo los turnos disponibles llevan borde
    val borde = if (!seleccionado && !reservado) BorderStroke(1.dp, colores.outline) else null

    Surface(
        onClick = onClick,
        enabled = !reservado, // Un turno reservado no se puede tocar
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = fondo,
        border = borde
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = horario.texto,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = colorHora
                )
                Text(
                    text = textoEstado,
                    style = MaterialTheme.typography.labelSmall,
                    color = colorEstado
                )
            }
            // A la derecha: check si está elegido, candado si está reservado, o el precio
            when {
                seleccionado -> Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                reservado -> Icon(
                    Icons.Default.Lock,
                    contentDescription = null,
                    tint = colores.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
                else -> Text(
                    text = "S/ $precio",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = colores.onSurface
                )
            }
        }
    }
}

// Aviso sobre cancelación.
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
                text = "Cancelación gratuita hasta 3 horas antes del turno.",
                style = MaterialTheme.typography.bodySmall,
                color = GreenDark
            )
        }
    }
}

// Barra inferior fija: qué eligió el usuario, el total y el botón de continuar.
@Composable
private fun BarraResumen(uiState: HorariosUiState, onContinuarClick: () -> Unit) {
    val colores = MaterialTheme.colorScheme
    val hora = uiState.horaSeleccionada // Guardamos en una variable local para poder revisarla abajo

    Surface(color = colores.surface, shadowElevation = 8.dp) {
        // navigationBarsPadding() para evitar que el botón quede tapado por los botones del celular
        Column(
            modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp)
        ) {
            if (hora != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Cada turno dura 1 hora, por eso el final es hora + 1
                    Text(
                        text = "1 hora: ${formatearHora(hora)} - ${formatearHora(hora + 1)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = colores.onSurfaceVariant
                    )
                    Text(
                        text = "Total: S/ ${uiState.total}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colores.primary
                    )
                }
            } else {
                Text(
                    text = "Elige un horario disponible para continuar",
                    style = MaterialTheme.typography.bodySmall,
                    color = colores.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onContinuarClick,
                enabled = hora != null, // Desactivado hasta que el usuario elija un turno
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Continuar la Reserva", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
            }
        }
    }
}

// VISTA PREVIA con un estado armado a mano: cancha, 3 fechas y la hora 7:00 PM elegida.
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HorariosPreview() {
    val hoy = LocalDate.now()
    AppSportZoneTheme {
        HorariosContent(
            uiState = HorariosUiState(
                cancha = DatosDePrueba.canchas[0],
                fechas = listOf(
                    OpcionFecha(hoy, "Hoy", "${hoy.dayOfMonth} oct"),
                    OpcionFecha(hoy.plusDays(1), "Mañana", "${hoy.plusDays(1).dayOfMonth} oct"),
                    OpcionFecha(hoy.plusDays(2), "Vie", "${hoy.plusDays(2).dayOfMonth} oct")
                ),
                fechaSeleccionada = hoy,
                mesAnio = "Octubre 2026",
                horarios = DatosDePrueba.horariosDe("1", hoy),
                horaSeleccionada = 19
            ),
            onBackClick = {},
            onFechaChange = {},
            onHoraChange = {},
            onContinuarClick = {}
        )
    }
}