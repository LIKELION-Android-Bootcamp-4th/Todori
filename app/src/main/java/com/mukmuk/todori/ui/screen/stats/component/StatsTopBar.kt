package com.mukmuk.todori.ui.screen.stats.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsTopBar(onMonthlyReportClick: () -> Unit = {}) {
    TopAppBar(
        title = { Text("통계", style = AppTextStyle.TitleSmall) },
        actions = {
            AssistChip(
                onClick = onMonthlyReportClick,
                label = { Text("월간 리포트") },
                leadingIcon = {
                    Icon(Icons.Outlined.Description, contentDescription = null, tint = Black)
                }
            )
        }
    )
}
