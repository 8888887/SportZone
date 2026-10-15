package com.example.appsportzone.data.repository

import com.example.appsportzone.data.prueba.DatosDePrueba
import com.example.appsportzone.domain.model.Cancha
import com.example.appsportzone.domain.model.Horario
import com.example.appsportzone.domain.repository.CanchaRepository
import java.time.LocalDate

// DATOS DE PRUEBA. Al conectar AWS se crea CanchaRepositoryImpl, que llamará al Lambda.
object CanchaRepositoryFake : CanchaRepository {

    override suspend fun getCanchas(): Result<List<Cancha>> =
        Result.success(DatosDePrueba.canchas)

    override suspend fun getCanchaById(id: String): Result<Cancha> {
        val cancha = DatosDePrueba.canchaPorId(id)
            ?: return Result.failure(Exception("Cancha no encontrada"))
        return Result.success(cancha)
    }

    override suspend fun getHorarios(canchaId: String, fecha: LocalDate): Result<List<Horario>> =
        Result.success(DatosDePrueba.horariosDe(canchaId, fecha))
}