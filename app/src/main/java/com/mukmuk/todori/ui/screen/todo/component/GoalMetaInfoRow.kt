package com.mukmuk.todori.ui.screen.todo.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.data.remote.goal.Goal
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Dimens.DefaultCornerRadius
import com.mukmuk.todori.ui.theme.GoalPrimary
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.Red
import com.mukmuk.todori.ui.theme.UserPrimary
import com.mukmuk.todori.ui.theme.White
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.todayIn
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GoalMetaInfoRow(goal: Goal, selectedDate: LocalDate) {
    val start = LocalDate.parse(goal.startDate)
    val end = LocalDate.parse(goal.endDate)
    val dDay = remember(goal.endDate, selectedDate) {
        ChronoUnit.DAYS.between(selectedDate.toJavaLocalDate(), end.toJavaLocalDate()).toInt()
    }

    val statusColor = when {
        selectedDate < start -> Gray
        dDay < 0 -> UserPrimary
        else -> GoalPrimary
    }

    val statusText = when {
        selectedDate < start -> "시작 전"
        dDay < 0 -> "완료"
        else -> "진행 중"
    }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 기간
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .border(1.dp, Gray, RoundedCornerShape(DefaultCornerRadius))
                .padding(Dimens.Nano)
        ) {
            Icon(Icons.Outlined.CalendarMonth, contentDescription = null, tint = Black, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(Dimens.Nano))
            Text("${goal.startDate} ~ ${goal.endDate}", style = AppTextStyle.BodySmall.copy(fontWeight = FontWeight.Bold))
        }

        Spacer(modifier = Modifier.width(Dimens.Tiny))

        // D-Day
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (dDay < 10) Red else UserPrimary)
                .padding(vertical = Dimens.Nano, horizontal = Dimens.Tiny)
        ) {
            Text(
                text = if (dDay < 0) "종료" else "D-$dDay",
                style = AppTextStyle.BodySmall.copy(fontWeight = FontWeight.Bold, color = White)
            )
        }

        Spacer(modifier = Modifier.width(Dimens.Tiny))

        // 상태
        Icon(Icons.Default.Adjust, contentDescription = null, tint = statusColor)
        Text(statusText, style = AppTextStyle.BodySmall, modifier = Modifier.padding(Dimens.Nano))
    }
}
