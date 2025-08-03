package com.mukmuk.todori.ui.screen.stats.tab

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mukmuk.todori.ui.screen.stats.card.SelectedWeek
import com.mukmuk.todori.ui.screen.stats.card.WeekCard
import com.mukmuk.todori.ui.screen.stats.card.WeekGraph
import com.mukmuk.todori.ui.screen.stats.card.WeekProgress
import com.mukmuk.todori.ui.theme.Dimens


@Composable
fun WeekTab() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        SelectedWeek()
        WeekCard()
        Spacer(modifier = Modifier.height(Dimens.Large))
        WeekGraph()
        Spacer(modifier = Modifier.height(Dimens.Large))
        WeekProgress()
    }
}