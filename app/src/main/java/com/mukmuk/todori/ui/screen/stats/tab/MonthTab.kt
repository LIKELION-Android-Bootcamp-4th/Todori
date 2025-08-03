package com.mukmuk.todori.ui.screen.stats.tab

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mukmuk.todori.ui.screen.stats.card.MonthCard
import com.mukmuk.todori.ui.screen.stats.card.MonthProgress
import com.mukmuk.todori.ui.screen.stats.card.SelectedMonth
import com.mukmuk.todori.ui.theme.Dimens

@Composable
fun MonthTab() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        SelectedMonth()
        MonthCard()
        Spacer(modifier = Modifier.height(Dimens.Large))
        MonthProgress()
    }
}