package com.example.designsystem.organisms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.designsystem.molecules.AppBottomBar
import com.example.designsystem.molecules.BottomDestination
import com.example.designsystem.molecules.TopHeader
import com.example.designsystem.theme.AppTheme

@Composable
fun AppScaffoldScreen(
    title: String,
    showBack: Boolean,
    selectedBottomDestination: BottomDestination,
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit,
    onDashboardClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        modifier = modifier,
        containerColor = AppTheme.colors.background,
        topBar = {
            TopHeader(
                title = title,
                onBack = if (showBack) onBackClick else null
            )
        },
        bottomBar = {
            AppBottomBar(
                selected = selectedBottomDestination,
                onHomeClick = onHomeClick,
                onDashboardClick = onDashboardClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(AppTheme.spacing.spaceM),
            content = content
        )
    }
}

@Composable
fun SimpleScaffoldScreen(
    title: String,
    showBack: Boolean = false,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        modifier = modifier,
        containerColor = AppTheme.colors.background,
        topBar = {
            TopHeader(
                title = title,
                onBack = if (showBack) onBackClick else null
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(AppTheme.spacing.spaceM),
            content = content
        )
    }
}
