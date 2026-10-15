package com.example.appsportzone.presentation.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.contentType
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appsportzone.ui.theme.AppSportZoneTheme
import org.w3c.dom.Text
import java.nio.file.WatchEvent

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit = {},
    onGuestClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LoginContent(
        uiState = uiState,
        onCorreoChange = viewModel::onCorreoChange,
        onContrasenaChange = viewModel::onContrasenaChange,
        onToggleContrasena = viewModel::toggleMostrarContrasena,
        onLoginClick = onLoginClick,
        onGuestClick = onGuestClick,
        onRegisterClick = onRegisterClick
    )
}

@Composable
fun LoginContent(
    uiState: LoginUiState,
    onCorreoChange: (String) -> Unit,
    onContrasenaChange: (String) -> Unit,
    onToggleContrasena: () -> Unit,
    onLoginClick: () -> Unit,
    onGuestClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val colores = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //Logo
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(colores.primaryContainer), contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SportsSoccer,
                contentDescription = null,
                tint = colores.primary,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(Modifier.height(12.dp))

        //Título
        Text(
            text = buildAnnotatedString {
                append("Sport ")
                withStyle(SpanStyle(color = colores.primary)) {append("Zone")}
            },
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Row(verticalAlignment = Alignment.CenterVertically){
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = colores.primary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(4.dp))

            Text(
                text = "Reserva tu cancha en Lima",
                style = MaterialTheme.typography.bodyMedium,
                color = colores.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(32.dp))

        //Tarjeta del formulario
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = colores.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Correo electrónico", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = uiState.correo,
                    onValueChange = onCorreoChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("ejemplo@correo.com")},
                    leadingIcon = {Icon(Icons.Default.Email, contentDescription = null)},
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Contraseña", style = MaterialTheme.typography.labelLarge)
                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        style = MaterialTheme.typography.labelMedium,
                        color = colores.primary
                    )
                }
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = uiState.contrasena,
                    onValueChange = onContrasenaChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("............")},
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null)},
                    trailingIcon = {
                        IconButton(onClick = onToggleContrasena) {
                            Icon(
                                imageVector = if (uiState.mostrarContrasena)
                                    Icons.Default.VisibilityOff
                                else
                                    Icons.Default.Visibility,
                                contentDescription = "Mostrar u ocultar contraseña"
                            )
                        }
                    },
                    visualTransformation = if (uiState.mostrarContrasena)
                                               VisualTransformation.None
                                           else
                                               PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = onLoginClick,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Iniciar Sesión", fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }

                Spacer(Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    HorizontalDivider(modifier = Modifier.weight(1f))
                    Text(
                        text = "  o  ",
                        style = MaterialTheme.typography.labelMedium,
                        color = colores.onSurfaceVariant
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f))
                }

                Spacer(Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onGuestClick,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Continuar como invitado")
                }

                Spacer(Modifier.height(10.dp))

                Text(
                    text = "Podrás explorar locales, pero para reservar debes iniciar sesión",
                    style = MaterialTheme.typography.bodySmall,
                    color = colores.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("¿No tienes cuenta?", color = colores.onSurfaceVariant)
            TextButton(onClick = onRegisterClick) {
                Text("Regístrate", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginPreview(){
    AppSportZoneTheme {
        LoginContent(
            uiState = LoginUiState(),
            onCorreoChange = {},
            onContrasenaChange = {},
            onToggleContrasena = {},
            onLoginClick = {},
            onGuestClick = {},
            onRegisterClick = {}
        )
    }
}