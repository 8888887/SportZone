package com.example.appsportzone.domain.usecase

import com.example.appsportzone.domain.model.Cancha
import com.example.appsportzone.domain.repository.CanchaRepository
import javax.inject.Inject

class GetCanchasUseCase @Inject constructor(private val repository: CanchaRepository) {
    suspend operator fun invoke(): Result<List<Cancha>> = repository.getCanchas()
}