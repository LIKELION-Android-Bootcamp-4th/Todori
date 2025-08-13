package com.mukmuk.todori.ui.screen.stats.component.day

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Dimens.DefaultCornerRadius
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.White

@Composable
fun LearningStreakCard(modifier: Modifier = Modifier) {
    //todo: 서버에서 받아올 값들
    val continueDay = 12
    val bestRecord = 28
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(DefaultCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = White
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(Dimens.Medium)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.LocalFireDepartment, contentDescription = null, tint = Color(0xfff09643))
                Text("연속 학습", style = AppTextStyle.BodyLarge)
            }
            Spacer(modifier = Modifier.height(Dimens.Small))
            Text("${continueDay} 일", style = AppTextStyle.TitleMedium.copy(color = Color(0xfff09643)))
            Spacer(modifier = Modifier.height(Dimens.Tiny))
            Text("최고 기록: ${bestRecord}일",style = AppTextStyle.BodySmall.copy(color = Gray))

        }
    }
}