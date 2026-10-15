package com.example.appsportzone.domain.usecase

import com.example.appsportzone.domain.model.Cancha
import com.example.appsportzone.domain.model.Reserva
import com.example.appsportzone.domain.repository.ReservaRepository
import java.time.LocalDate
import javax.inject.Inject

class CrearReservaUseCase @Inject constructor(private val repository: ReservaRepository) {
    suspend operator fun invoke(cancha: Cancha, fecha: LocalDate, hora: Int, metodoPago: String): Result<Reserva> =
        repository.crearReserva(cancha, fecha, hora, metodoPago)
}