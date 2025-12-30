package com.example.observerproject.ui.home

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
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToDetail: () -> Unit,
    onNavigateToDashboard: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.onScreenVisible()
    }

    AppScaffoldScreen(
        title = stringResource(R.string.screen_title_home),
        showBack = false,
        selectedBottomDestination = BottomDestination.Home,
        onHomeClick = { /* Already on Home */ },
        onDashboardClick = onNavigateToDashboard
    ) {
        Text(
            text = stringResource(R.string.home_welcome_title),
            style = AppTheme.typography.heading3,
            color = AppTheme.colors.textPrimary
        )

        Spacer(modifier = Modifier.height(AppTheme.spacing.spaceS))

        Text(
            text = stringResource(R.string.home_welcome_description),
            style = AppTheme.typography.body1,
            color = AppTheme.colors.textSecondary
        )

        Spacer(modifier = Modifier.height(AppTheme.spacing.spaceXL))

        Text(
            text = stringResource(R.string.home_section_simulate),
            style = AppTheme.typography.heading3,
            color = AppTheme.colors.textPrimary
        )

        Spacer(modifier = Modifier.height(AppTheme.spacing.spaceM))

        SecondaryButton(
            text = stringResource(R.string.home_button_low_incident),
            onClick = { viewModel.onLowIncidentClicked() }
        )

        Spacer(modifier = Modifier.height(AppTheme.spacing.spaceS))

        PrimaryButton(
            text = stringResource(R.string.home_button_medium_incident),
            onClick = { viewModel.onMediumIncidentClicked() }
        )

        Spacer(modifier = Modifier.height(AppTheme.spacing.spaceS))

        DangerButton(
            text = stringResource(R.string.home_button_high_incident),
            onClick = { viewModel.onHighIncidentClicked() }
        )

        Spacer(modifier = Modifier.height(AppTheme.spacing.spaceXL))

        Text(
            text = stringResource(R.string.home_section_navigation),
            style = AppTheme.typography.heading3,
            color = AppTheme.colors.textPrimary
        )

        Spacer(modifier = Modifier.height(AppTheme.spacing.spaceM))

        PrimaryButton(
            text = stringResource(R.string.home_button_go_to_detail),
            onClick = onNavigateToDetail
        )

        Spacer(modifier = Modifier.height(AppTheme.spacing.spaceS))

        SecondaryButton(
            text = stringResource(R.string.home_button_view_dashboard),
            onClick = onNavigateToDashboard
        )

        Spacer(modifier = Modifier.height(AppTheme.spacing.spaceL))
    }
}
