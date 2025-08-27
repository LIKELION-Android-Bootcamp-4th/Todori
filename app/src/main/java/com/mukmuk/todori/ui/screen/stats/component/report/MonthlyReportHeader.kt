package com.mukmuk.todori.ui.screen.stats.component.report

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.ReportPrimary

@Composable
fun MonthlyReportHeader(
    month: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "뒤로가기",
                    tint = Black
                )
            }

            Text(
                text = "뒤로가기",
                style = AppTextStyle.BodyLarge,
                color = Black,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Text(
            text = "$month 리포트",
            style = AppTextStyle.TitleLarge,
            color = ReportPrimary
        )
        Text(
            text = "당신의 학습 여정을 돌아봐요",
            style = AppTextStyle.BodySmallNormal,
            color = DarkGray
        )
        Spacer(modifier = Modifier.height(Dimens.Large))
        HorizontalDivider(color = DarkGray.copy(alpha = 0.7f))
    }
}
