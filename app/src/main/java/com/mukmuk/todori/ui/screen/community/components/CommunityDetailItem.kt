package com.mukmuk.todori.ui.screen.community.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.PeopleAlt
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Timestamp
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Dimens.DefaultCornerRadius
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.GroupPrimary
import com.mukmuk.todori.ui.theme.NotoSans
import com.mukmuk.todori.ui.theme.White
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CommunityDetailItem(
    name: String,
    description: String,
    createdAt: Timestamp?,
    joinedAt: Timestamp?,
    memberCount: Int,
    activeDays: List<String>,
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

    Card (
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp)
            .border(1.dp, Gray, RoundedCornerShape(10.dp)),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = White),
    ){
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = name, color = Black, fontSize = 16.sp, fontFamily = NotoSans, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.width(Dimens.Tiny))

            Text(text = description, color = Black, fontSize = 16.sp, fontFamily = NotoSans)

            Spacer(modifier = Modifier.width(Dimens.Tiny))

            Row(verticalAlignment = Alignment.CenterVertically) {
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

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(GroupPrimary)
                        .padding(vertical = Dimens.Nano, horizontal = Dimens.Tiny)
                ) {
                    Text("D+$dDay", style = AppTextStyle.BodySmall.copy(fontWeight = FontWeight.Bold, color = White))
                }

                Spacer(modifier = Modifier.width(Dimens.Tiny))

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

            Spacer(modifier = Modifier.width(Dimens.Tiny))

            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("참여하기")
            }
        }
    }

}