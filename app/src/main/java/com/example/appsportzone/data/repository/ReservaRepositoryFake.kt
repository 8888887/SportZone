package com.example.appsportzone.data.repository

import com.example.appsportzone.data.prueba.DatosDePrueba
import com.example.appsportzone.domain.model.Cancha
import com.example.appsportzone.domain.model.EstadoReserva
import com.example.appsportzone.domain.model.Reserva
import com.example.appsportzone.domain.repository.ReservaRepository
import java.time.LocalDate

// IMPLEMENTACIÓN DE PRUEBA: guarda las reservas en memoria mientras la app está abierta.
// Al conectar AWS se crea ReservaRepositoryImpl (como LibroRepositoryImpl del profe)
// que llama al Lambda, y las pantallas no cambian.
object ReservaRepositoryFake : ReservaRepository {

    private val hoy = LocalDate.now()
    private val c = DatosDePrueba.canchas

    // Reservas iniciales para que la pantalla no salga vacía
    private val reservas = mutableListOf<Reserva>()

    override suspend fun getReservas(): Result<List<Reserva>> = Result.success(reservas.toList())

    override suspend fun crearReserva(
        cancha: Cancha, fecha: LocalDate, hora: Int, metodoPago: String
    ): Result<Reserva> {
        // DATOS DE PRUEBA: el código lo inventamos. Con AWS lo generará el Lambda,
        // que además rechazará el turno si alguien lo ocupó primero.
        val nueva = Reserva(
            codigo = "SZ-LIMA-${(10000..99999).random()}",
            cancha = cancha,
            fecha = fecha,
            hora = hora,
            total = cancha.precioHora,
            metodoPago = metodoPago,
            estado = EstadoReserva.CONFIRMADA
        )
        reservas.add(nueva)
        return Result.success(nueva)
    }

    override suspend fun cancelarReserva(codigo: String): Result<Unit> {
        val indice = reservas.indexOfFirst { it.codigo == codigo }
        if (indice == -1) return Result.failure(Exception("Reserva no encontrada"))
        reservas[indice] = reservas[indice].copy(estado = EstadoReserva.CANCELADA)
        return Result.success(Unit)
    }
}