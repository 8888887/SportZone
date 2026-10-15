package com.example.appsportzone.domain.usecase

import com.example.appsportzone.domain.model.Cancha
import com.example.appsportzone.domain.repository.CanchaRepository
import javax.inject.Inject

class GetCanchaUseCase @Inject constructor(private val repository: CanchaRepository) {
    suspend operator fun invoke(id: String): Result<Cancha> = repository.getCanchaById(id)
}