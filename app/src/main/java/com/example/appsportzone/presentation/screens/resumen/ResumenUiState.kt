package com.example.appsportzone.presentation.screens.resumen

import com.example.appsportzone.domain.model.Cancha

// Métodos de pago que ofrece la pantalla. Un enum es una lista cerrada de opciones:
// el usuario solo puede elegir una de estas.
// Cada opción trae su título y una línea de detalle para mostrar en pantalla.
enum class MetodoPago(val titulo: String, val detalle: String) {
    YAPE("Yape", "Pago con tu celular"),
    TARJETA("Tarjeta de Crédito / Débito", "Visa, Mastercard, Amex")
}

// ESTADO DE LA PANTALLA
// todo lo que necesita para dibujarse
data class ResumenUiState(
    val cancha: Cancha? = null,                      // Cancha elegida (null mientras no se encuentra)
    val textoFecha: String = "",                     // Fecha lista para mostrar: "Viernes, 16 de octubre"
    val hora: Int = 0,                               // Hora de inicio en formato 24h
    val metodoPago: MetodoPago = MetodoPago.TARJETA, // Metodo seleccionado
    val mostrarConfirmacion: Boolean = false,        // Se muestra el diálogo de "Reserva Confirmada"
    val codigoReserva: String = ""                   // Código que aparece
) {
    // Cada turno dura 1 hora, así que el total es el precio de una hora.
    val total: Int get() = cancha?.precioHora ?: 0
}