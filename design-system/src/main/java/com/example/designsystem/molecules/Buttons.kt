package com.example.designsystem.molecules

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.AppTheme

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        enabled = enabled,
        shape = AppTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = AppTheme.colors.primary,
            contentColor = AppTheme.colors.textOnPrimary,
            disabledContainerColor = AppTheme.colors.primary.copy(alpha = 0.5f),
            disabledContentColor = AppTheme.colors.textOnPrimary.copy(alpha = 0.7f)
        )
    ) {
        Text(
            text = text.uppercase(),
            style = AppTheme.typography.button,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        enabled = enabled,
        shape = AppTheme.shapes.medium,
        border = BorderStroke(
            width = 1.5.dp,
            color = if (enabled) AppTheme.colors.primary else AppTheme.colors.primary.copy(alpha = 0.5f)
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = AppTheme.colors.primary,
            disabledContentColor = AppTheme.colors.primary.copy(alpha = 0.5f)
        )
    ) {
        Text(
            text = text.uppercase(),
            style = AppTheme.typography.button,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun DangerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        enabled = enabled,
        shape = AppTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = AppTheme.colors.error,
            contentColor = AppTheme.colors.textOnPrimary,
            disabledContainerColor = AppTheme.colors.error.copy(alpha = 0.5f),
            disabledContentColor = AppTheme.colors.textOnPrimary.copy(alpha = 0.7f)
        )
    ) {
        Text(
            text = text.uppercase(),
            style = AppTheme.typography.button,
            textAlign = TextAlign.Center
        )
    }
}
