package com.example.appsportzone.domain.usecase

import com.example.appsportzone.domain.repository.ReservaRepository
import javax.inject.Inject

class CancelarReservaUseCase @Inject constructor(private val repository: ReservaRepository) {
    suspend operator fun invoke(codigo: String): Result<Unit> = repository.cancelarReserva(codigo)
}