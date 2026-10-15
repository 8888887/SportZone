package com.example.appsportzone.domain.repository

import com.example.appsportzone.domain.model.Cancha
import com.example.appsportzone.domain.model.Horario
import java.time.LocalDate

interface CanchaRepository {
    suspend fun getCanchas(): Result<List<Cancha>>
    suspend fun getCanchaById(id: String): Result<Cancha>
    suspend fun getHorarios(canchaId: String, fecha: LocalDate): Result<List<Horario>>
}