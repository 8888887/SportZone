package com.example.appsportzone.di

import com.example.appsportzone.data.remote.api.CanchaApiService
import com.example.appsportzone.data.repository.CanchaRepositoryImpl
import com.example.appsportzone.data.repository.ReservaRepositoryFake
import com.example.appsportzone.domain.repository.CanchaRepository
import com.example.appsportzone.domain.repository.ReservaRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Le dice a Hilt QUÉ implementación entregar cuando alguien pide un repositorio.
// DATOS DE PRUEBA: al conectar AWS, solo cambiamos estas dos líneas por las versiones *Impl.
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideCanchaRepository(api: CanchaApiService): CanchaRepository = CanchaRepositoryImpl(api)

    @Provides
    @Singleton
    fun provideReservaRepository(): ReservaRepository = ReservaRepositoryFake
}