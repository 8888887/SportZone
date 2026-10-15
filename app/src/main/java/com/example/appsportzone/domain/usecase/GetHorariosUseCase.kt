package com.example.appsportzone.domain.usecase

import com.example.appsportzone.domain.model.Horario
import com.example.appsportzone.domain.repository.CanchaRepository
import java.time.LocalDate
import javax.inject.Inject

class GetHorariosUseCase @Inject constructor(private val repository: CanchaRepository) {
    suspend operator fun invoke(canchaId: String, fecha: LocalDate): Result<List<Horario>> =
        repository.getHorarios(canchaId, fecha)
}