package com.example.appsportzone.presentation.navigation

object RutasNav {
    const val LOGIN = "login"
    const val INICIO = "inicio"

    // Ruta con un hueco: {canchaId}. Para REGISTRAR usamos la versión con llaves,
    // y para IR usamos la función de abajo, que reemplaza el hueco por el valor real.
    const val HORARIOS = "horarios/{canchaId}"
    fun horarios(canchaId: String) = "horarios/$canchaId"

    // Esta ruta tiene tres huecos. Ejemplo real: resumen/2/2026-10-16/19
    // Los nombres entre llaves deben ser iguales a los que lee ResumenViewModel.
    const val RESUMEN = "resumen/{canchaId}/{fecha}/{hora}"
    fun resumen(canchaId: String, fecha: String, hora: Int) = "resumen/$canchaId/$fecha/$hora"

    const val RESERVAS = "reservas"
}