package com.mukmuk.todori.ui.screen.stats.component.week

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.White


@Composable
fun StatItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = AppTextStyle.BodySmall.copy(color = White)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = AppTextStyle.BodyLargeWhite
        )
    }
}