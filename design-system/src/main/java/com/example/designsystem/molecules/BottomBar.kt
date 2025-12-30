package com.example.designsystem.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.AppTheme

enum class BottomDestination {
    Home,
    Dashboard
}

@Composable
fun AppBottomBar(
    selected: BottomDestination,
    onHomeClick: () -> Unit,
    onDashboardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp),
        color = AppTheme.colors.surface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = AppTheme.spacing.spaceS),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BottomBarItem(
                icon = if (selected == BottomDestination.Home) Icons.Filled.Home else Icons.Outlined.Home,
                label = "Home",
                isSelected = selected == BottomDestination.Home,
                onClick = onHomeClick,
                modifier = Modifier.weight(1f)
            )

            BottomBarItem(
                icon = if (selected == BottomDestination.Dashboard) Icons.Filled.Dashboard else Icons.Outlined.Dashboard,
                label = "Dashboard",
                isSelected = selected == BottomDestination.Dashboard,
                onClick = onDashboardClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun BottomBarItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = if (isSelected) AppTheme.colors.primary else AppTheme.colors.iconDefault
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = false, radius = 48.dp),
                onClick = onClick
            )
            .padding(vertical = AppTheme.spacing.spaceXS),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.height(AppTheme.spacing.spaceXS))

        Text(
            text = label,
            style = AppTheme.typography.caption,
            color = color
        )

        if (isSelected) {
            Spacer(modifier = Modifier.height(AppTheme.spacing.spaceXXS))
            Box(
                modifier = Modifier
                    .size(width = 32.dp, height = 3.dp)
                    .background(
                        color = AppTheme.colors.primary,
                        shape = AppTheme.shapes.pill
                    )
            )
        }
    }
}
