package com.example.appsportzone.data.remote.dto

// Formato común de todas las respuestas del API
data class ApiResponseDto<T>(
    val success: Boolean,
    val message: String?,
    val data: T?,
    val timestamp: String?
)