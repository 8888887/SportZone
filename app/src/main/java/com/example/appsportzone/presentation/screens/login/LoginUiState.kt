package com.example.appsportzone.presentation.screens.login

data class LoginUiState(
    val correo: String = "",
    val contrasena: String = "",
    val mostrarContrasena: Boolean = false,
    val isLoading: Boolean = false
)
