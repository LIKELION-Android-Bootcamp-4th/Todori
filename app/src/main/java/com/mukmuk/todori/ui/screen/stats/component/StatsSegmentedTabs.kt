package com.mukmuk.todori.ui.screen.stats.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.ui.screen.stats.tab.StatsTab
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.LightGray
import com.mukmuk.todori.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsSegmentedTabs(
    selected: StatsTab,
    onSelect: (StatsTab) -> Unit,
    modifier: Modifier = Modifier
) {
    SingleChoiceSegmentedButtonRow(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp)
    ) {
        StatsTab.entries.forEachIndexed { index, tab ->
            SegmentedButton(
                selected = selected == tab,
                onClick = { onSelect(tab) },
                shape = SegmentedButtonDefaults.itemShape(index, StatsTab.entries.size),
                label = { Text(tab.label, style = AppTextStyle.Body) },
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = White,
                    activeContentColor = Black,
                    inactiveContainerColor = LightGray,
                    inactiveContentColor = Black,
                    activeBorderColor = LightGray,
                    inactiveBorderColor = LightGray
                )
            )
        }
    }
}