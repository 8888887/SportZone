package com.example.appsportzone.domain.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

// Estados guardados de una reserva. "Finalizada" no se guarda: se deduce cuando la fecha ya pasó.
enum class EstadoReserva {
    CONFIRMADA,
    CANCELADA
}

// MODELO DE DOMINIO: una reserva hecha por el cliente.
data class Reserva(
    val codigo: String,       // Ej: "SZ-LIMA-84920"
    val cancha: Cancha,
    val fecha: LocalDate,
    val hora: Int,            // Hora de inicio en 24h
    val total: Int,
    val metodoPago: String,
    val estado: EstadoReserva
) {
    // Fecha y hora exactas de inicio del turno
    val inicio: LocalDateTime get() = LocalDateTime.of(fecha, LocalTime.of(hora, 0))

    // Último momento para cancelar gratis
    val limiteCancelacion: LocalDateTime get() = inicio.minusHours(HORAS_CANCELACION.toLong())

    // Próxima = confirmada y todavía no empieza
    fun esProxima(ahora: LocalDateTime = LocalDateTime.now()): Boolean =
        estado == EstadoReserva.CONFIRMADA && inicio.isAfter(ahora)

    fun sePuedeCancelar(ahora: LocalDateTime = LocalDateTime.now()): Boolean =
        esProxima(ahora) && ahora.isBefore(limiteCancelacion)

    companion object {
        const val HORAS_CANCELACION = 3
    }
}