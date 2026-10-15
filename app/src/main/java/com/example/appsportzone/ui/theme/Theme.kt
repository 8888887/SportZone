package com.example.appsportzone.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MovableContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = Color.White,
    primaryContainer = GreenLight,
    onPrimaryContainer = GreenDark,
    background = Color.White,
    onBackground = Neutral900,
    surface = Color.White,
    onSurface = Neutral900,
    onSurfaceVariant = Neutral500,
    outline = Neutral200
)

@Composable
fun AppSportZoneTheme(content: @Composable () -> Unit){
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        content = content
    )
}