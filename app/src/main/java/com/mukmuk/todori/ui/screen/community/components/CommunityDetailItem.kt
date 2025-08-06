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
import com.mukmuk.todori.ui.screen.todo.component.StudyMetaInfoRow
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

    Card (
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = Dimens.Large)
            .border(1.dp, Gray, RoundedCornerShape(10.dp)),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = White),
    ){
        Column(
            modifier = Modifier.padding(Dimens.Medium)
        ) {
            Text(name, style = AppTextStyle.BodyLarge.copy(fontWeight = FontWeight.Bold))

            Spacer(modifier = Modifier.height(Dimens.Tiny))

            Text(description, style = AppTextStyle.Body.copy(color = DarkGray))

            Spacer(modifier = Modifier.height(Dimens.Tiny))

            StudyMetaInfoRow(
                createdAt = createdAt,
                joinedAt = joinedAt,
                memberCount = memberCount,
                activeDays = activeDays
            )

            Spacer(modifier = Modifier.height(Dimens.Tiny))

            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("참여하기", style = AppTextStyle.ButtonText.copy(color = White))
            }
        }
    }

}