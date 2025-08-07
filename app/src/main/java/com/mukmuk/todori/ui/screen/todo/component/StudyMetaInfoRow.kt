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
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.PeopleAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Dimens.DefaultCornerRadius
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.GroupPrimary
import com.mukmuk.todori.ui.theme.White
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StudyMetaInfoRow(
    createdAt: Timestamp?,
    joinedAt: Timestamp? = null,
    memberCount: Int,
    activeDays: List<String>,
    modifier: Modifier = Modifier
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy. MM. dd")
    val createdDate = remember(createdAt) {
        createdAt?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
    }
    val joinedDate = remember(joinedAt) {
        joinedAt?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
    }
    val today = remember { LocalDate.now() }
    val dDay = joinedDate?.let { ChronoUnit.DAYS.between(it, today).toInt() } ?: 0

    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        // 생성일
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .border(1.dp, Gray, RoundedCornerShape(DefaultCornerRadius))
                .padding(Dimens.Nano)
        ) {
            Icon(Icons.Outlined.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp), tint = Black)
            Spacer(modifier = Modifier.width(Dimens.Nano))
            Text(createdDate?.format(formatter) ?: "", style = AppTextStyle.BodySmall.copy(fontWeight = FontWeight.Bold))
        }

        Spacer(modifier = Modifier.width(Dimens.Tiny))

        // D-day
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(GroupPrimary)
                .padding(vertical = Dimens.Nano, horizontal = Dimens.Tiny)
        ) {
            Text("D+$dDay", style = AppTextStyle.BodySmall.copy(fontWeight = FontWeight.Bold, color = White))
        }

        Spacer(modifier = Modifier.width(Dimens.Tiny))

        // 멤버 수
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .border(1.dp, Gray, RoundedCornerShape(DefaultCornerRadius))
                .padding(vertical = Dimens.Nano, horizontal = Dimens.Tiny)
        ) {
            Icon(Icons.Outlined.PeopleAlt, contentDescription = null, modifier = Modifier.size(16.dp), tint = Black)
            Spacer(modifier = Modifier.width(Dimens.Nano))
            Text("$memberCount", style = AppTextStyle.BodySmall.copy(fontWeight = FontWeight.Bold))
        }

        Spacer(modifier = Modifier.width(Dimens.Tiny))

        // 반복 요일
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .border(1.dp, Gray, RoundedCornerShape(DefaultCornerRadius))
                .padding(vertical = Dimens.Nano, horizontal = Dimens.Tiny)
        ) {
            Icon(Icons.Default.Repeat, contentDescription = null, modifier = Modifier.size(16.dp), tint = Black)
            Spacer(modifier = Modifier.width(Dimens.Nano))
            Text(
                text = activeDays.joinToString(" "),
                style = AppTextStyle.BodySmall.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}
