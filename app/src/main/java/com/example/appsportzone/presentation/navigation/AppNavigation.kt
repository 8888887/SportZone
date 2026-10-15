package com.example.appsportzone.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.appsportzone.presentation.screens.horarios.HorariosScreen
import com.example.appsportzone.presentation.screens.inicio.InicioScreen
import com.example.appsportzone.presentation.screens.login.LoginScreen
import com.example.appsportzone.presentation.screens.reservas.MisReservasScreen
import com.example.appsportzone.presentation.screens.resumen.ResumenScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val irAInicio: () -> Unit = {
        navController.navigate(RutasNav.INICIO) {
            popUpTo(RutasNav.LOGIN) { inclusive = true }
        }
    }

    val volverAlInicio: () -> Unit = {
        navController.popBackStack(RutasNav.INICIO, inclusive = false)
    }

    // ir a Mis reservas quitando de la pila horarios y resumen
    val irAMisReservas: () -> Unit = {
        navController.navigate(RutasNav.RESERVAS) {
            popUpTo(RutasNav.INICIO)
            launchSingleTop = true // Evita abrir la misma pantalla dos veces
        }
    }

    // qué hacer al tocar cada ítem de la barra inferior
    val irDesdeBarra: (String) -> Unit = { ruta ->
        when (ruta) {
            RutasNav.INICIO -> volverAlInicio()
            RutasNav.RESERVAS -> irAMisReservas()
            else -> {} // PENDIENTE: "Perfil" todavía no existe
        }
    }

    NavHost(navController = navController, startDestination = RutasNav.LOGIN) {

        composable(RutasNav.LOGIN) {
            LoginScreen(
                onLoginClick = irAInicio,
                onGuestClick = irAInicio,
                onRegisterClick = {}
            )
        }

        composable(RutasNav.INICIO) {
            InicioScreen(
                onDisponibilidadClick = { canchaId ->
                    navController.navigate(RutasNav.horarios(canchaId))
                },
                onBottomBarClick = irDesdeBarra
            )
        }

        composable(
            route = RutasNav.HORARIOS,
            arguments = listOf(navArgument("canchaId") { type = NavType.StringType })
        ) {
            HorariosScreen(
                onBackClick = { navController.popBackStack() },
                onContinuarClick = { canchaId, fecha, hora ->
                    navController.navigate(RutasNav.resumen(canchaId, fecha, hora))
                }
            )
        }

        composable(
            route = RutasNav.RESUMEN,
            arguments = listOf(
                navArgument("canchaId") { type = NavType.StringType },
                navArgument("fecha") { type = NavType.StringType },
                navArgument("hora") { type = NavType.IntType }
            )
        ) {
            ResumenScreen(
                onBackClick = { navController.popBackStack() },
                onVerReservasClick = irAMisReservas, // Va a Mis reservas
                onVolverInicioClick = volverAlInicio
            )
        }

        composable(RutasNav.RESERVAS) {
            MisReservasScreen(
                onBackClick = { navController.popBackStack() },
                onBottomBarClick = irDesdeBarra,
                onVolverAReservar = { canchaId ->
                    navController.navigate(RutasNav.horarios(canchaId))
                }
            )
        }
    }
}