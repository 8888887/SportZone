package com.example.appsportzone.presentation.screens.login

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onCorreoChange(valor: String){
        _uiState.update { it.copy(correo = valor) }
    }

    fun onContrasenaChange(valor: String){
        _uiState.update { it.copy(contrasena = valor) }
    }

    fun toggleMostrarContrasena(){
        _uiState.update { it.copy(mostrarContrasena = !it.mostrarContrasena) }
    }
}