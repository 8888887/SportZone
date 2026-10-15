package com.example.appsportzone.data.prueba

import com.example.appsportzone.domain.model.Cancha
import com.example.appsportzone.domain.model.EstadoHorario
import com.example.appsportzone.domain.model.Horario
import java.time.LocalDate

object DatosDePrueba {
    val canchas = listOf(
        Cancha("1", "San Miguel", "Av. Costanera 1253", 2.4, "Fútbol", "Fútbol 7",
            "Césped sintético", 80, 4.8, listOf("Estacionamiento", "Vestuarios", "Luces LED")),
        Cancha("2", "Miraflores", "Calle Recavarren 240", 3.1, "Fútbol", "Fútbol 5",
            "Techada", 100, 4.6, listOf("Techado", "Vestuarios", "Cafetería")),
        Cancha("3", "San Miguel", "Av. Costanera 1253", 2.4, "Vóley", "Vóley",
            "Arena", 50, 4.5, listOf("Estacionamiento", "Vestuarios")),
        Cancha("4", "Miraflores", "Calle Recavarren 240", 3.1, "Básquet", "Básquet",
            "Parquet", 90, 4.7, listOf("Techado", "Luces LED")),
        Cancha("5", "Miraflores", "Calle Recavarren 240", 3.1, "Tenis", "Tenis",
            "Polvo de ladrillo", 70, 4.4, listOf("Vestuarios", "Estacionamiento"))
    )

    fun canchaPorId(id: String): Cancha? = canchas.find { it.id == id }

    fun horariosDe(canchaId: String, fecha: LocalDate): List<Horario> {
        val semilla = (canchaId.toIntOrNull() ?: 0) + fecha.dayOfMonth
        return (16..22).map { hora ->
            val ocupado = (hora + semilla) % 3 == 0
            Horario(
                hora = hora,
                estado = if (ocupado) EstadoHorario.RESERVADO else EstadoHorario.DISPONIBLE
            )
        }
    }
}