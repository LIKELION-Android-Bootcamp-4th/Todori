package com.mukmuk.todori.ui.screen.stats.card

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.ui.component.ProgressWithText
import com.mukmuk.todori.ui.screen.stats.DailyRecord
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Dimens.DefaultCornerRadius
import com.mukmuk.todori.ui.theme.UserPrimary
import com.mukmuk.todori.ui.theme.White
import kotlinx.datetime.LocalDate

@Composable
fun WeekProgress(record: List<DailyRecord>) {
    Column() {
        Card(
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(horizontal = Dimens.Medium),
            shape = RoundedCornerShape(DefaultCornerRadius),
            colors = CardDefaults.cardColors(
                containerColor = White
            ),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(Dimens.Medium)) {
                Text("주간 TODO 통계", style = AppTextStyle.TitleSmall)
                Spacer(modifier = Modifier.height(Dimens.XLarge))

                record.sortedBy { it.selectedDay }.forEach { record ->


                    ProgressWithText(
                        progress = record.completedTodos / record.totalTodos.toFloat(),
                        completed = record.completedTodos,
                        total = record.totalTodos,
                        progressColor = UserPrimary,
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = Dimens.Nano,
                        label = "일"
                    )
                    Spacer(modifier = Modifier.height(Dimens.Large))

                    ProgressWithText(
                        progress = record.completedTodos / record.totalTodos.toFloat(),
                        completed = record.completedTodos,
                        total = record.totalTodos,
                        progressColor = UserPrimary,
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = Dimens.Nano,
                        label = "월"
                    )
                    Spacer(modifier = Modifier.height(Dimens.Large))

                    ProgressWithText(
                        progress = 8 / 10.toFloat(),
                        completed = 8,
                        total = 10,
                        progressColor = UserPrimary,
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = Dimens.Nano,
                        label = "화"
                    )
                    Spacer(modifier = Modifier.height(Dimens.Large))

                    ProgressWithText(
                        progress = 8 / 10.toFloat(),
                        completed = 8,
                        total = 10,
                        progressColor = UserPrimary,
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = Dimens.Nano,
                        label = "수"
                    )
                    Spacer(modifier = Modifier.height(Dimens.Large))

                    ProgressWithText(
                        progress = 8 / 10.toFloat(),
                        completed = 8,
                        total = 10,
                        progressColor = UserPrimary,
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = Dimens.Nano,
                        label = "목"
                    )
                    Spacer(modifier = Modifier.height(Dimens.Large))

                    ProgressWithText(
                        progress = 8 / 10.toFloat(),
                        completed = 8,
                        total = 10,
                        progressColor = UserPrimary,
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = Dimens.Nano,
                        label = "금"
                    )
                    Spacer(modifier = Modifier.height(Dimens.Large))

                    ProgressWithText(
                        progress = 8 / 10.toFloat(),
                        completed = 8,
                        total = 10,
                        progressColor = UserPrimary,
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = Dimens.Nano,
                        label = "토"
                    )
                    Spacer(modifier = Modifier.height(Dimens.Medium))
                }
            }
        }
        Spacer(modifier = Modifier.height(Dimens.Large))
    }
}