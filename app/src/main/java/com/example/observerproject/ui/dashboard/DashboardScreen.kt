package com.example.observerproject.ui.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.designsystem.molecules.BottomDestination
import com.example.designsystem.molecules.DangerButton
import com.example.designsystem.molecules.PrimaryButton
import com.example.designsystem.molecules.SecondaryButton
import com.example.designsystem.organisms.AppScaffoldScreen
import com.example.designsystem.theme.AppTheme
import com.example.incidentsdk.Severity
import com.example.observerproject.R

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateBack: () -> Unit,
    onHomeClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    AppScaffoldScreen(
        title = stringResource(R.string.screen_title_dashboard),
        showBack = true,
        selectedBottomDestination = BottomDestination.Dashboard,
        onBackClick = onNavigateBack,
        onHomeClick = onHomeClick,
        onDashboardClick = { /* Already on Dashboard */ }
    ) {
        when {
            uiState.isLoading -> {
                LoadingContent()
            }
            uiState.errorMessage != null -> {
                ErrorContent(
                    message = stringResource(R.string.error_loading_summary, uiState.errorMessage.orEmpty()),
                    onRetry = { viewModel.refresh() }
                )
            }
            else -> {
                DashboardContent(
                    uiState = uiState,
                    onLowIncident = { viewModel.onLowIncidentClicked() },
                    onMediumIncident = { viewModel.onMediumIncidentClicked() },
                    onHighIncident = { viewModel.onHighIncidentClicked() },
                    onTimeFilterSelected = { viewModel.onTimeFilterSelected(it) }
                )
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = AppTheme.colors.primary,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(AppTheme.spacing.spaceM))
            Text(
                text = stringResource(R.string.loading),
                style = AppTheme.typography.body1,
                color = AppTheme.colors.textSecondary
            )
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.error_title),
            style = AppTheme.typography.heading2,
            color = AppTheme.colors.error
        )
        Spacer(modifier = Modifier.height(AppTheme.spacing.spaceS))
        Text(
            text = message,
            style = AppTheme.typography.body1,
            color = AppTheme.colors.textSecondary
        )
        Spacer(modifier = Modifier.height(AppTheme.spacing.spaceM))
        SecondaryButton(
            text = stringResource(R.string.retry),
            onClick = onRetry
        )
    }
}

@Composable
private fun DashboardContent(
    uiState: DashboardUiState,
    onLowIncident: () -> Unit,
    onMediumIncident: () -> Unit,
    onHighIncident: () -> Unit,
    onTimeFilterSelected: (TimeFilter) -> Unit
) {
    TotalIncidentsCard(total = uiState.totalIncidents)

    Spacer(modifier = Modifier.height(AppTheme.spacing.spaceL))

    Text(
        text = stringResource(R.string.dashboard_incidents_by_severity),
        style = AppTheme.typography.heading3,
        color = AppTheme.colors.textPrimary
    )

    Spacer(modifier = Modifier.height(AppTheme.spacing.spaceM))

    if (uiState.incidentsBySeverity.isEmpty()) {
        EmptyStateText()
    } else {
        uiState.incidentsBySeverity.forEach { (severity, count) ->
            SeverityRow(severity = severity, count = count)
            Spacer(modifier = Modifier.height(AppTheme.spacing.spaceS))
        }
    }

    Spacer(modifier = Modifier.height(AppTheme.spacing.spaceL))

    Text(
        text = stringResource(R.string.dashboard_incidents_by_screen),
        style = AppTheme.typography.heading3,
        color = AppTheme.colors.textPrimary
    )

    Spacer(modifier = Modifier.height(AppTheme.spacing.spaceM))

    if (uiState.incidentsByScreen.isEmpty()) {
        EmptyStateText()
    } else {
        uiState.incidentsByScreen.forEach { (screen, count) ->
            ScreenRow(screenName = screen, count = count)
            Spacer(modifier = Modifier.height(AppTheme.spacing.spaceS))
        }
    }

    Spacer(modifier = Modifier.height(AppTheme.spacing.spaceXL))

    Text(
        text = stringResource(R.string.dashboard_simulate_incidents),
        style = AppTheme.typography.heading3,
        color = AppTheme.colors.textPrimary
    )

    Spacer(modifier = Modifier.height(AppTheme.spacing.spaceM))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.spaceS)
    ) {
        SecondaryButton(
            text = stringResource(R.string.dashboard_button_low),
            onClick = onLowIncident,
            modifier = Modifier.width(120.dp)
        )
        PrimaryButton(
            text = stringResource(R.string.dashboard_button_medium),
            onClick = onMediumIncident,
            modifier = Modifier.width(120.dp)
        )
        DangerButton(
            text = stringResource(R.string.dashboard_button_high),
            onClick = onHighIncident,
            modifier = Modifier.width(120.dp)
        )
    }

    Spacer(modifier = Modifier.height(AppTheme.spacing.spaceXL))

    Text(
        text = stringResource(R.string.dashboard_incidents_over_time),
        style = AppTheme.typography.heading3,
        color = AppTheme.colors.textPrimary
    )

    Spacer(modifier = Modifier.height(AppTheme.spacing.spaceM))

    TimeFilterChips(
        selectedFilter = uiState.selectedTimeFilter,
        onFilterSelected = onTimeFilterSelected
    )

    Spacer(modifier = Modifier.height(AppTheme.spacing.spaceM))

    IncidentTimeChart(
        chartData = uiState.chartData,
        selectedFilter = uiState.selectedTimeFilter,
        incidentsInRange = uiState.incidentsInTimeRange,
        maxValue = uiState.maxChartValue
    )

    Spacer(modifier = Modifier.height(AppTheme.spacing.spaceL))
}

@Composable
private fun TimeFilterChips(
    selectedFilter: TimeFilter,
    onFilterSelected: (TimeFilter) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.spaceS)
    ) {
        TimeFilter.entries.forEach { filter ->
            FilterChip(
                label = filter.label,
                isSelected = filter == selectedFilter,
                onClick = { onFilterSelected(filter) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) AppTheme.colors.primary else AppTheme.colors.surface
    val textColor = if (isSelected) AppTheme.colors.textOnPrimary else AppTheme.colors.textPrimary
    val borderColor = if (isSelected) AppTheme.colors.primary else AppTheme.colors.divider

    Box(
        modifier = modifier
            .height(40.dp)
            .background(
                color = backgroundColor,
                shape = AppTheme.shapes.medium
            )
            .border(
                width = 1.dp,
                color = borderColor,
                shape = AppTheme.shapes.medium
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = AppTheme.typography.label,
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun IncidentTimeChart(
    chartData: List<TimeChartDataPoint>,
    selectedFilter: TimeFilter,
    incidentsInRange: Int,
    maxValue: Int
) {
    val dividerColor = AppTheme.colors.divider
    val textSecondaryColor = AppTheme.colors.textSecondary
    val homeColor = AppTheme.colors.primary
    val detailColor = AppTheme.colors.primaryVariant
    val dashboardColor = AppTheme.colors.secondary

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colors.surface
        ),
        shape = AppTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppTheme.spacing.spaceM)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.dashboard_last_minutes, selectedFilter.minutes),
                    style = AppTheme.typography.body2,
                    color = AppTheme.colors.textSecondary
                )
                Text(
                    text = stringResource(R.string.dashboard_incidents_count, incidentsInRange),
                    style = AppTheme.typography.heading3,
                    color = AppTheme.colors.primary
                )
            }

            Spacer(modifier = Modifier.height(AppTheme.spacing.spaceM))

            ChartLegend()

            Spacer(modifier = Modifier.height(AppTheme.spacing.spaceM))

            if (chartData.isEmpty() || incidentsInRange == 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.dashboard_no_incidents_in_period),
                        style = AppTheme.typography.body2,
                        color = AppTheme.colors.textSecondary
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    YAxisLabels(maxValue = maxValue)

                    Canvas(
                        modifier = Modifier
                            .weight(1f)
                            .height(150.dp)
                    ) {
                        drawBarChart(
                            chartData = chartData,
                            maxCount = maxValue,
                            homeColor = homeColor,
                            detailColor = detailColor,
                            dashboardColor = dashboardColor,
                            gridColor = dividerColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(AppTheme.spacing.spaceS))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.dashboard_minutes_ago, selectedFilter.minutes),
                        style = AppTheme.typography.caption,
                        color = textSecondaryColor
                    )
                    Text(
                        text = stringResource(R.string.dashboard_now),
                        style = AppTheme.typography.caption,
                        color = textSecondaryColor
                    )
                }
            }
        }
    }
}

@Composable
private fun ChartLegend() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val homeColor = AppTheme.colors.primary
        val detailColor = AppTheme.colors.primaryVariant
        val dashboardColor = AppTheme.colors.secondary

        Box(
            modifier = Modifier
                .size(12.dp)
                .background(homeColor, AppTheme.shapes.small)
        )
        Spacer(modifier = Modifier.width(AppTheme.spacing.spaceXS))
        Text(
            text = stringResource(R.string.legend_home),
            style = AppTheme.typography.caption,
            color = AppTheme.colors.textSecondary
        )

        Spacer(modifier = Modifier.width(AppTheme.spacing.spaceM))

        Box(
            modifier = Modifier
                .size(12.dp)
                .background(detailColor, AppTheme.shapes.small)
        )
        Spacer(modifier = Modifier.width(AppTheme.spacing.spaceXS))
        Text(
            text = stringResource(R.string.legend_detail),
            style = AppTheme.typography.caption,
            color = AppTheme.colors.textSecondary
        )

        Spacer(modifier = Modifier.width(AppTheme.spacing.spaceM))

        Box(
            modifier = Modifier
                .size(12.dp)
                .background(dashboardColor, AppTheme.shapes.small)
        )
        Spacer(modifier = Modifier.width(AppTheme.spacing.spaceXS))
        Text(
            text = stringResource(R.string.legend_dashboard),
            style = AppTheme.typography.caption,
            color = AppTheme.colors.textSecondary
        )
    }
}

@Composable
private fun YAxisLabels(maxValue: Int) {
    val gridLines = 3
    val step = if (maxValue > 0) maxValue.toFloat() / gridLines else 1f

    Column(
        modifier = Modifier
            .width(32.dp)
            .height(150.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        for (i in gridLines downTo 0) {
            val value = (step * i).toInt()
            Text(
                text = value.toString(),
                style = AppTheme.typography.caption,
                color = AppTheme.colors.textSecondary,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private fun DrawScope.drawBarChart(
    chartData: List<TimeChartDataPoint>,
    maxCount: Int,
    homeColor: Color,
    detailColor: Color,
    dashboardColor: Color,
    gridColor: Color
) {
    val width = size.width
    val height = size.height
    val barCount = chartData.size

    if (barCount == 0) return

    val effectiveMaxCount = if (maxCount == 0) 1 else maxCount

    val totalBarSpace = width * 0.95f
    val barWidth = (totalBarSpace / barCount).coerceAtMost(8.dp.toPx())
    val spacing = if (barCount > 1) (totalBarSpace - barWidth * barCount) / (barCount - 1) else 0f
    val startX = (width - totalBarSpace) / 2

    val gridLines = 3
    for (i in 0..gridLines) {
        val y = height - (height * i / gridLines)
        drawLine(
            color = gridColor.copy(alpha = 0.3f),
            start = Offset(0f, y),
            end = Offset(width, y),
            strokeWidth = 1.dp.toPx()
        )
    }

    chartData.forEachIndexed { index, dataPoint ->
        val x = startX + index * (barWidth + spacing)

        val detailBarHeight = (dataPoint.detailCount.toFloat() / effectiveMaxCount) * height * 0.9f
        val homeBarHeight = (dataPoint.homeCount.toFloat() / effectiveMaxCount) * height * 0.9f
        val dashboardBarHeight = (dataPoint.dashboardCount.toFloat() / effectiveMaxCount) * height * 0.9f

        var currentY = height

        if (dataPoint.detailCount > 0) {
            currentY -= detailBarHeight
            drawRect(
                color = detailColor,
                topLeft = Offset(x, currentY),
                size = Size(barWidth, detailBarHeight)
            )
        }

        if (dataPoint.homeCount > 0) {
            currentY -= homeBarHeight
            drawRect(
                color = homeColor,
                topLeft = Offset(x, currentY),
                size = Size(barWidth, homeBarHeight)
            )
        }

        if (dataPoint.dashboardCount > 0) {
            currentY -= dashboardBarHeight
            drawRect(
                color = dashboardColor,
                topLeft = Offset(x, currentY),
                size = Size(barWidth, dashboardBarHeight)
            )
        }
    }
}

@Composable
private fun TotalIncidentsCard(total: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colors.primary
        ),
        shape = AppTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppTheme.spacing.spaceL),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.dashboard_total_incidents),
                style = AppTheme.typography.body1,
                color = AppTheme.colors.textOnPrimary.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(AppTheme.spacing.spaceS))
            Text(
                text = total.toString(),
                style = AppTheme.typography.heading1.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = AppTheme.colors.textOnPrimary
            )
        }
    }
}

@Composable
private fun SeverityRow(severity: Severity, count: Int) {
    val (color, labelResId) = when (severity) {
        Severity.CRITICAL -> AppTheme.colors.error to R.string.severity_critical
        Severity.HIGH -> Color(0xFFE65100) to R.string.severity_high
        Severity.MEDIUM -> AppTheme.colors.warning to R.string.severity_medium
        Severity.LOW -> AppTheme.colors.success to R.string.severity_low
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colors.surface
        ),
        shape = AppTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppTheme.spacing.spaceM),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            color = color,
                            shape = AppTheme.shapes.pill
                        )
                )
                Spacer(modifier = Modifier.width(AppTheme.spacing.spaceS))
                Text(
                    text = stringResource(labelResId),
                    style = AppTheme.typography.body1,
                    color = AppTheme.colors.textPrimary
                )
            }
            Text(
                text = count.toString(),
                style = AppTheme.typography.heading3,
                color = color
            )
        }
    }
}

@Composable
private fun ScreenRow(screenName: String, count: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colors.surface
        ),
        shape = AppTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppTheme.spacing.spaceM),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = screenName,
                style = AppTheme.typography.body1,
                color = AppTheme.colors.textPrimary
            )
            Text(
                text = count.toString(),
                style = AppTheme.typography.heading3,
                color = AppTheme.colors.primary
            )
        }
    }
}

@Composable
private fun EmptyStateText() {
    Text(
        text = stringResource(R.string.dashboard_no_incidents),
        style = AppTheme.typography.body2,
        color = AppTheme.colors.textSecondary
    )
}
