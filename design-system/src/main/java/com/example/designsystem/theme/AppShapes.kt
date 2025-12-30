package com.example.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Immutable
data class AppShapes(
    val small: Shape,
    val medium: Shape,
    val large: Shape,
    val extraLarge: Shape,
    val pill: Shape
) {
    companion object {
        val smallCornerRadius = 4.dp
        val mediumCornerRadius = 8.dp
        val largeCornerRadius = 16.dp
        val extraLargeCornerRadius = 24.dp
    }
}

val DefaultAppShapes = AppShapes(
    small = RoundedCornerShape(AppShapes.smallCornerRadius),
    medium = RoundedCornerShape(AppShapes.mediumCornerRadius),
    large = RoundedCornerShape(AppShapes.largeCornerRadius),
    extraLarge = RoundedCornerShape(AppShapes.extraLargeCornerRadius),
    pill = RoundedCornerShape(percent = 50)
)

val LocalAppShapes = staticCompositionLocalOf { DefaultAppShapes }
