package com.example.appsportzone.data.remote.api

import com.example.appsportzone.data.remote.dto.ApiResponseDto
import com.example.appsportzone.data.remote.dto.CanchaDto
import com.example.appsportzone.data.remote.dto.CanchasDataDto
import retrofit2.http.GET
import retrofit2.http.Path

// Cada función es un endpoint del API Gateway
interface CanchaApiService {

    @GET("canchas")
    suspend fun getCanchas(): ApiResponseDto<CanchasDataDto>

    @GET("canchas/{id_cancha}")
    suspend fun getCanchaById(@Path("id_cancha") id: String): ApiResponseDto<CanchaDto>
}