package com.example.observerproject.ui.detail

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.designsystem.molecules.BottomDestination
import com.example.designsystem.molecules.DangerButton
import com.example.designsystem.molecules.PrimaryButton
import com.example.designsystem.molecules.SecondaryButton
import com.example.designsystem.organisms.AppScaffoldScreen
import com.example.designsystem.theme.AppTheme
import com.example.observerproject.R

@Composable
fun DetailScreen(
    viewModel: DetailViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.onScreenVisible()
    }

    AppScaffoldScreen(
        title = stringResource(R.string.screen_title_detail),
        showBack = true,
        selectedBottomDestination = BottomDestination.Home,
        onBackClick = onNavigateBack,
        onHomeClick = onNavigateToHome,
        onDashboardClick = onNavigateToDashboard
    ) {
        Text(
            text = stringResource(R.string.detail_title),
            style = AppTheme.typography.heading3,
            color = AppTheme.colors.textPrimary
        )

        Spacer(modifier = Modifier.height(AppTheme.spacing.spaceS))

        Text(
            text = stringResource(R.string.detail_description),
            style = AppTheme.typography.body1,
            color = AppTheme.colors.textSecondary
        )

        Spacer(modifier = Modifier.height(AppTheme.spacing.spaceXL))

        Text(
            text = stringResource(R.string.detail_section_simulate),
            style = AppTheme.typography.heading3,
            color = AppTheme.colors.textPrimary
        )

        Spacer(modifier = Modifier.height(AppTheme.spacing.spaceM))

        SecondaryButton(
            text = stringResource(R.string.detail_button_low_incident),
            onClick = { viewModel.onLowIncidentClicked() }
        )

        Spacer(modifier = Modifier.height(AppTheme.spacing.spaceS))

        PrimaryButton(
            text = stringResource(R.string.detail_button_high_incident),
            onClick = { viewModel.onHighIncidentClicked() }
        )

        Spacer(modifier = Modifier.height(AppTheme.spacing.spaceS))

        DangerButton(
            text = stringResource(R.string.detail_button_critical_incident),
            onClick = { viewModel.onCriticalIncidentClicked() }
        )

        Spacer(modifier = Modifier.height(AppTheme.spacing.spaceXL))

        Text(
            text = stringResource(R.string.detail_danger_zone_title),
            style = AppTheme.typography.heading3,
            color = AppTheme.colors.error
        )

        Spacer(modifier = Modifier.height(AppTheme.spacing.spaceS))

        Text(
            text = stringResource(R.string.detail_danger_zone_description),
            style = AppTheme.typography.body2,
            color = AppTheme.colors.textSecondary
        )

        Spacer(modifier = Modifier.height(AppTheme.spacing.spaceM))

        DangerButton(
            text = stringResource(R.string.detail_button_simulate_crash),
            onClick = { viewModel.onCrashClicked() }
        )

        Spacer(modifier = Modifier.height(AppTheme.spacing.spaceL))
    }
}
