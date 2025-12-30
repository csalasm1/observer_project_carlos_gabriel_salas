package com.example.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class AppSpacing(
    val spaceXXS: Dp = 2.dp,
    val spaceXS: Dp = 4.dp,
    val spaceS: Dp = 8.dp,
    val spaceM: Dp = 16.dp,
    val spaceL: Dp = 24.dp,
    val spaceXL: Dp = 32.dp,
    val spaceXXL: Dp = 48.dp,
    val spaceXXXL: Dp = 64.dp
)

val DefaultAppSpacing = AppSpacing()

val LocalAppSpacing = staticCompositionLocalOf { DefaultAppSpacing }
