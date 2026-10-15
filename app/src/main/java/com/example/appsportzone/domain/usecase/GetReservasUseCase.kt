package com.example.appsportzone.domain.usecase

import com.example.appsportzone.domain.model.Reserva
import com.example.appsportzone.domain.repository.ReservaRepository
import javax.inject.Inject

class GetReservasUseCase @Inject constructor(private val repository: ReservaRepository) {
    suspend operator fun invoke(): Result<List<Reserva>> = repository.getReservas()
}