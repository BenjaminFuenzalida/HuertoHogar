package com.example.huertohogar.ui.theme

import androidx.compose.ui.graphics.Color

// --- Colores Primarios ---
val BlancoSuave = Color(0xFFF7F7F7)
val VerdeEsmeralda = Color(0xFF2E8B57)
val AmarilloMostaza = Color(0xFFFFD700)
val MarronClaro = Color(0xFF8B4513)

// --- Colores de Texto ---
val GrisOscuro = Color(0xFF333333)
val GrisMedio = Color(0xFF666666)

// --- Paleta de Material 3 ---

val LightColors = androidx.compose.material3.lightColorScheme(
    primary = VerdeEsmeralda,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB3DEC1),
    onPrimaryContainer = Color.Black,
    secondary = AmarilloMostaza,
    onSecondary = Color.Black,
    tertiary = MarronClaro,
    onTertiary = Color.White,
    background = BlancoSuave,
    onBackground = GrisOscuro,
    surface = BlancoSuave,
    onSurface = GrisOscuro,
    surfaceVariant = Color(0xFFE0E0E0),
    onSurfaceVariant = GrisMedio,
    error = Color(0xFFB00020),
    onError = Color.White
)

val DarkColors = androidx.compose.material3.darkColorScheme(
    primary = VerdeEsmeralda,
    onPrimary = Color.White,
    secondary = AmarilloMostaza,
    onSecondary = Color.Black,
    background = Color(0xFF121212),
    onBackground = BlancoSuave,
    surface = Color(0xFF1E1E1E),
    onSurface = BlancoSuave,
)
