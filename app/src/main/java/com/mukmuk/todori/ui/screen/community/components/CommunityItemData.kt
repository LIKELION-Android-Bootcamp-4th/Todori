package com.mukmuk.todori.ui.screen.community.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.mukmuk.todori.data.remote.study.Study
import com.mukmuk.todori.ui.screen.todo.component.StudyMetaInfoRow
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.White

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CommunityItemData(
    study: Study,
    memberCount: Int,
    ) {
    Card (
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.Tiny)
            .border(1.dp, Gray, RoundedCornerShape(10.dp))
            .clickable {

            },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = White),
    ) {
        Column(
            modifier = Modifier.padding(Dimens.Medium)
        ) {
            Text(study.title, style = AppTextStyle.BodyLarge.copy(fontWeight = FontWeight.Bold))

            Spacer(modifier = Modifier.height(Dimens.Tiny))

            Text(study.description, style = AppTextStyle.Body.copy(color = DarkGray))

            Spacer(modifier = Modifier.height(Dimens.Tiny))

            StudyMetaInfoRow(
                createdAt = study.createdAt,
                memberCount = memberCount,
                activeDays = study.activeDays
            )
        }
    }
}