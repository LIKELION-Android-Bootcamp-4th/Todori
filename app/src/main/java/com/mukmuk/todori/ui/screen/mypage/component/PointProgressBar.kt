package com.mukmuk.todori.ui.screen.mypage.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.ui.component.CustomLinearProgressBar
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Dimens

@Composable
fun PointProgressBar(
    level: Int,
    currentPoint: Int,
    maxPoint: Int
) {
    val progress = currentPoint.toFloat() / maxPoint.toFloat()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.Medium)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Lv.$level", style = AppTextStyle.Body)
            Text(text = "$currentPoint / $maxPoint", style = AppTextStyle.Body)
        }

        Spacer(modifier = Modifier.height(8.dp))

        CustomLinearProgressBar(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
        )
    }
}
