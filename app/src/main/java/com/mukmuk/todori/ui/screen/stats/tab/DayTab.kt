package com.mukmuk.todori.ui.screen.stats.tab

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mukmuk.todori.ui.screen.stats.card.CalendarCard
import com.mukmuk.todori.ui.screen.stats.card.DayStatsCard
import com.mukmuk.todori.ui.theme.Dimens
import kotlinx.datetime.LocalDate

@Composable
fun DayTab(
    selectedDay: LocalDate
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(Dimens.XLarge))
        CalendarCard()
        Spacer(modifier = Modifier.height(Dimens.Large))
        DayStatsCard(selectedDay = selectedDay)
    }
}