package com.example.appsportzone.presentation.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

private data class ItemBarra(
    val ruta: String,
    val titulo: String,
    val icono: ImageVector
)

private val itemsBarra = listOf(
    ItemBarra("inicio", "Inicio", Icons.Default.Home),
    ItemBarra("reservas", "Mis reservas", Icons.Default.CalendarMonth),
    ItemBarra("perfil", "Perfil", Icons.Default.Person)
)

@Composable
fun AppBottomBar(
    rutaActual: String,
    onItemClick: (String) -> Unit = {}
){
    val colores = MaterialTheme.colorScheme

    NavigationBar(containerColor = colores.surface) {
        itemsBarra.forEach { item ->
            NavigationBarItem(
                selected = item.ruta == rutaActual,
                onClick = { onItemClick(item.ruta) },
                icon = { Icon(item.icono, contentDescription = item.titulo) },
                label = { Text(item.titulo) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = colores.primary,
                    selectedTextColor = colores.primary,
                    indicatorColor = colores.primaryContainer
                )
            )
        }
    }
}