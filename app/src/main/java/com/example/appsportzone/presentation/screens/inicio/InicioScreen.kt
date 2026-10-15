package com.example.appsportzone.presentation.screens.inicio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.appsportzone.presentation.common.AppBottomBar
import com.example.appsportzone.presentation.common.CanchaCard
import com.example.appsportzone.presentation.common.iconoDisciplina
import com.example.appsportzone.ui.theme.AppSportZoneTheme

@Composable
fun InicioScreen(
    onDisponibilidadClick: (String) -> Unit = {},
    onBottomBarClick: (String) -> Unit = {},
    viewModel: InicioViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    InicioContent(
        uiState = uiState,
        onBusquedaChange = viewModel::onBusquedaChange,
        onDisciplinaChange = viewModel::onDisciplinaChange,
        onDisponibilidadClick = onDisponibilidadClick,
        onBottomBarClick = onBottomBarClick
    )
}

@Composable
fun InicioContent(
    uiState: InicioUiState,
    onBusquedaChange: (String) -> Unit,
    onDisciplinaChange: (String) -> Unit,
    onDisponibilidadClick: (String) -> Unit,
    onBottomBarClick: (String) -> Unit
){
    val colores = MaterialTheme.colorScheme

    Scaffold(
        containerColor = colores.background,
        bottomBar = { AppBottomBar(rutaActual = "inicio", onItemClick = onBottomBarClick) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { EncabezadoSede(uiState.sede) }

            item { Buscador(uiState.busqueda, onBusquedaChange) }

            item {
                FiltrosDisciplina(
                    disciplinas = uiState.disciplinas,
                    seleccionada = uiState.disciplinaSeleccionada,
                    onSeleccion = onDisciplinaChange
                )
            }

            item { BannerPromocion() }

            item { TituloSeccion() }

            if(uiState.canchas.isEmpty()){
                item {
                    Text(
                        text = "No encontramos canchas con esos filtros",
                        color = colores.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth().padding(24.dp)
                    )
                }
            } else {
                items(uiState.canchas, key = { it.id }) { cancha ->
                    CanchaCard(
                        cancha = cancha,
                        onDescripcionClick = {},
                        onDisponibilidadClick = { onDisponibilidadClick(cancha.id)}
                    )
                }
            }
        }
    }
}

@Composable
private fun EncabezadoSede(sede: String){
    val colores = MaterialTheme.colorScheme

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Sede Seleccionada",
                style = MaterialTheme.typography.labelMedium,
                color = colores.onSurfaceVariant
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = colores.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = sede,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
            }
        }
        IconButton(onClick = {}) {
            Icon(Icons.Default.Notifications, contentDescription = "Notificaciones")
        }
    }
}

@Composable
private fun Buscador(valor: String, onCambio: (String) -> Unit){
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {Text("Buscar canchas, deportes o distritos...")},
        leadingIcon = {Icon(Icons.Default.Search, contentDescription = null)},
        trailingIcon = {Icon(Icons.Default.Tune, contentDescription = "Filtros")},
        singleLine = true,
        shape = RoundedCornerShape(14.dp)
    )
}

@Composable
private fun FiltrosDisciplina(
        disciplinas: List<String>,
        seleccionada: String,
        onSeleccion: (String) -> Unit
){
    val colores = MaterialTheme.colorScheme

    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(disciplinas){disciplina ->
            FilterChip(
                selected = disciplina == seleccionada,
                onClick = {onSeleccion(disciplina)},
                label = {Text(disciplina)},
                leadingIcon = {
                    Icon(
                        iconoDisciplina(disciplina),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = colores.primary,
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White
                )
            )
        }
    }
}

@Composable
private fun BannerPromocion(){
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "PROMOCIÓN LIMA",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Reserva tu partido nocturno",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Obtén 15% de descuento en horarios de 9:00 PM a medianoche",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
            Spacer(Modifier.width(12.dp))
            Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.2f)) {
                Text(
                    text = "-15%",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun TituloSeccion(){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Canchas disponibles hoy",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        TextButton(onClick = {}){
            Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text("Ver mapa")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun InicioPreview(){
    AppSportZoneTheme {
        InicioContent(
            uiState = InicioUiState(),
            onBusquedaChange = {},
            onDisciplinaChange = {},
            onDisponibilidadClick = {},
            onBottomBarClick = {}
        )
    }
}