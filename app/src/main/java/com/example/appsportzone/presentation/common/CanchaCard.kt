package com.example.appsportzone.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material.icons.filled.SportsVolleyball
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appsportzone.domain.model.Cancha
import com.example.appsportzone.ui.theme.GreenDark

fun iconoDisciplina(disciplina: String): ImageVector = when (disciplina){
    "Fútbol" -> Icons.Default.SportsSoccer
    "Vóley" -> Icons.Default.SportsVolleyball
    "Básquet" -> Icons.Default.SportsBasketball
    "Tenis" -> Icons.Default.SportsTennis
    else -> Icons.Default.SportsSoccer
}

@Composable
fun CanchaCard(
    cancha: Cancha,
    onDescripcionClick: () -> Unit,
    onDisponibilidadClick: () -> Unit,
    modifier: Modifier = Modifier
){
    val colores = MaterialTheme.colorScheme

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colores.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().height(150.dp)
                .background(Brush.verticalGradient(listOf(colores.primary, GreenDark)))
        ){
            Icon(
                imageVector = iconoDisciplina(cancha.disciplina),
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.25f),
                modifier = Modifier.size(72.dp).align(Alignment.Center)
            )

            Insignia(
                texto = "${cancha.tipo} · ${cancha.superficie}",
                modifier = Modifier.align(Alignment.TopStart).padding(12.dp)
            )

            if (cancha.calificacion > 0) {
                Surface(
                    modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.9f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(2.dp))
                        Text(
                            text = cancha.calificacion.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = colores.onSurface
                        )
                    }
                }
            }
        }
        //Información
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "SportZone - Sede ${cancha.local}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = colores.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = if (cancha.distanciaKm > 0) "${cancha.direccion} • A ${cancha.distanciaKm} km" else cancha.direccion,
                    style = MaterialTheme.typography.bodySmall,
                    color = colores.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "S/ ${cancha.precioHora}",
                    style = MaterialTheme.typography.titleLarge,
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

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                cancha.servicios.take(3).forEach { servicio ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = colores.primaryContainer
                    ) {
                        Text(
                            text = servicio,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = GreenDark
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onDescripcionClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text("Ver descripción", maxLines = 1, style = MaterialTheme.typography.labelLarge)
                }
                Button(
                    onClick = onDisponibilidadClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text("Ver disponibilidad", maxLines = 1, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
private fun Insignia(texto: String, modifier: Modifier = Modifier){
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = Color.White.copy(alpha = 0.9f)
    ) {
        Text(
            text = texto,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}