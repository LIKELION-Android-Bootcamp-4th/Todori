package com.mukmuk.todori.ui.screen.stats.card

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.R
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Dimens

@Composable
fun SelectedMonth() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.Medium)
    ) {
        //왼쪽 화살표
        Row(
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            IconButton(onClick = {}) {
                Icon(
                    painter = painterResource(R.drawable.icon_arrow_left),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        //월 표시
        Row(
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text("2025년 8월", style = AppTextStyle.TitleSmall)
        }

        //오른쪽 화살표
        Row(
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            IconButton(onClick = {}) {
                Icon(
                    painter = painterResource(R.drawable.icon_arrow_right),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}