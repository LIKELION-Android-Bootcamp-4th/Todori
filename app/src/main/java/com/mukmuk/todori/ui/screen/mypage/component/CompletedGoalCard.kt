package com.mukmuk.todori.ui.screen.mypage.component

import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material3.Icon
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.data.remote.goal.Goal
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Dimens.DefaultCornerRadius
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.LightGray
import com.mukmuk.todori.ui.theme.UserPrimary


@Composable
fun CompletedGoalCard(
    goal: Goal
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, color = LightGray, RoundedCornerShape(Dimens.DefaultCornerRadius))
    ) {
        Column(
            modifier = Modifier
                .padding(Dimens.Medium)
        ){
            Text(
                text = goal.title,
                style = AppTextStyle.TitleSmall
            )

            Text(
                text = goal.description ?: "",
                style = AppTextStyle.Body
            )

            Spacer(modifier = Modifier.height(Dimens.Medium))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .border(1.dp,Gray, RoundedCornerShape(DefaultCornerRadius))
                    .padding(Dimens.Nano)
            ) {
                Icon(Icons.Outlined.CalendarMonth, contentDescription = null, tint = Black, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(Dimens.Nano))
                Text("${goal.startDate} ~ ${goal.endDate}", style = AppTextStyle.BodySmall.copy(fontWeight = FontWeight.Bold))
            }

            Icon(Icons.Default.Adjust, contentDescription = null, tint = UserPrimary)
            Text("완료", style = AppTextStyle.BodySmall, modifier = Modifier.padding(Dimens.Nano))

        }
    }
}