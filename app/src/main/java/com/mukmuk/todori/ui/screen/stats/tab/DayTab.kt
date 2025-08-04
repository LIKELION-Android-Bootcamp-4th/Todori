package com.mukmuk.todori.ui.screen.stats.tab

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mukmuk.todori.ui.screen.stats.card.CalendarCard
import com.mukmuk.todori.ui.screen.stats.card.DayStatsCard
import com.mukmuk.todori.ui.theme.Dimens
import kotlinx.datetime.LocalDate

@Composable
fun DayTab() {
    var selectedDay by remember {
        mutableStateOf(LocalDate.parse("2025-08-04"))
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(Dimens.XLarge))
        CalendarCard()
        Spacer(modifier = Modifier.height(Dimens.Large))
        DayStatsCard(
            selectedDay = selectedDay,
            studySeconds = 12345,
            completedTodos = 3,
            totalTodos = 10,
            )
    }
}