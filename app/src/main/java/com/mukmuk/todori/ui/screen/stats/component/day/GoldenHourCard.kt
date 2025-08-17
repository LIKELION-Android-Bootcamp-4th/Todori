package com.mukmuk.todori.ui.screen.stats.component.day

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Dimens.DefaultCornerRadius
import com.mukmuk.todori.ui.theme.GoldenHourAccent
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.White


@Composable
fun GoldenHourCard(
    startHour: Int?,
    endHour: Int? = null,
    completionDeltaPercent: Int? = null,
    modifier: Modifier = Modifier,
) {

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(DefaultCornerRadius),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(Dimens.Medium)
        ) {
            Row {
                Icon(Icons.Outlined.FlashOn, contentDescription = null, tint = GoldenHourAccent)
                Spacer(Modifier.width(6.dp))
                Text("골든 아워", style = AppTextStyle.BodyLarge)
            }
            Spacer(Modifier.height(Dimens.Small))

            if (startHour == null || endHour == null) {
                Text("데이터가 더 필요해요", style = AppTextStyle.BodySmall.copy(color = Gray))
                Spacer(Modifier.height(Dimens.Tiny))
                return@Column
            }

            val timeLabel = if (startHour == endHour) {
                "%02d시 집중".format(startHour)
            } else {
                "%02d:00-%02d:00".format(startHour, endHour)
            }

            Text(
                text = timeLabel,
                style = AppTextStyle.TitleMedium.copy(color = GoldenHourAccent)
            )

            Spacer(Modifier.height(Dimens.Nano))
            Text(
                text = "완료율 ${completionDeltaPercent}%",
                style = AppTextStyle.BodySmall.copy(color = GoldenHourAccent)
            )
        }
    }
}
