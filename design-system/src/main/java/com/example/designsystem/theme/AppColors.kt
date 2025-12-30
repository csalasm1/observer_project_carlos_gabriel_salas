package com.example.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class AppColors(
    val primary: Color,
    val primaryVariant: Color,
    val secondary: Color,
    val secondaryVariant: Color,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val error: Color,
    val success: Color,
    val warning: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textOnPrimary: Color,
    val textOnSecondary: Color,
    val divider: Color,
    val iconDefault: Color,
    val iconSelected: Color
)

val LightAppColors = AppColors(
    primary = Color(0xFF0D7377),
    primaryVariant = Color(0xFF14A3A8),
    secondary = Color(0xFFFF6B35),
    secondaryVariant = Color(0xFFE85D04),
    background = Color(0xFFF8F9FA),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE9ECEF),
    error = Color(0xFFDC3545),
    success = Color(0xFF198754),
    warning = Color(0xFFFFC107),
    textPrimary = Color(0xFF212529),
    textSecondary = Color(0xFF6C757D),
    textOnPrimary = Color(0xFFFFFFFF),
    textOnSecondary = Color(0xFFFFFFFF),
    divider = Color(0xFFDEE2E6),
    iconDefault = Color(0xFF6C757D),
    iconSelected = Color(0xFF0D7377)
)

val DarkAppColors = AppColors(
    primary = Color(0xFF14A3A8),
    primaryVariant = Color(0xFF0D7377),
    secondary = Color(0xFFFF8C5A),
    secondaryVariant = Color(0xFFFF6B35),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    surfaceVariant = Color(0xFF2D2D2D),
    error = Color(0xFFE57373),
    success = Color(0xFF81C784),
    warning = Color(0xFFFFD54F),
    textPrimary = Color(0xFFE1E1E1),
    textSecondary = Color(0xFFADB5BD),
    textOnPrimary = Color(0xFF000000),
    textOnSecondary = Color(0xFF000000),
    divider = Color(0xFF343A40),
    iconDefault = Color(0xFFADB5BD),
    iconSelected = Color(0xFF14A3A8)
)

val LocalAppColors = staticCompositionLocalOf { LightAppColors }
