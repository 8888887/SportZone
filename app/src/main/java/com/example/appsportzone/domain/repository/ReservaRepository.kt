package com.example.appsportzone.domain.repository

import com.example.appsportzone.domain.model.Cancha
import com.example.appsportzone.domain.model.Reserva
import java.time.LocalDate

interface ReservaRepository {
    suspend fun getReservas(): Result<List<Reserva>>
    suspend fun crearReserva(cancha: Cancha, fecha: LocalDate, hora: Int, metodoPago: String): Result<Reserva>
    suspend fun cancelarReserva(codigo: String): Result<Unit>
}