package com.example.appsportzone.data.repository

import com.example.appsportzone.data.remote.api.CanchaApiService
import com.example.appsportzone.domain.model.Cancha
import com.example.appsportzone.domain.model.Horario
import com.example.appsportzone.domain.repository.CanchaRepository
import java.time.LocalDate
import javax.inject.Inject

// Obtiene las canchas del API de AWS
class CanchaRepositoryImpl @Inject constructor(
    private val api: CanchaApiService
) : CanchaRepository {

    override suspend fun getCanchas(): Result<List<Cancha>> = runCatching {
        val respuesta = api.getCanchas()
        if (respuesta.success && respuesta.data != null) {
            respuesta.data.items
                .filter { it.estado == "activo" } // Solo mostramos canchas activas
                .map { it.toDomain() }
        } else {
            throw Exception(respuesta.message ?: "No se pudieron obtener las canchas")
        }
    }

    override suspend fun getCanchaById(id: String): Result<Cancha> = runCatching {
        val respuesta = api.getCanchaById(id)
        if (respuesta.success && respuesta.data != null) {
            respuesta.data.toDomain()
        } else {
            throw Exception(respuesta.message ?: "Cancha no encontrada")
        }
    }

    // PENDIENTE: todavía no existe el endpoint de horarios, así que seguimos con datos de prueba
    override suspend fun getHorarios(canchaId: String, fecha: LocalDate): Result<List<Horario>> =
        CanchaRepositoryFake.getHorarios(canchaId, fecha)
}