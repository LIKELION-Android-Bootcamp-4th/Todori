package com.mukmuk.todori.ui.screen.community.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.Timestamp
import com.mukmuk.todori.data.remote.study.Study
import com.mukmuk.todori.data.remote.study.StudyMember
import com.mukmuk.todori.ui.screen.community.detail.CommunityDetailViewModel
import com.mukmuk.todori.ui.screen.todo.component.StudyMetaInfoRow
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.White
import kotlinx.datetime.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StudyDetailCard(
    uid: String,
    study: Study,
    selectedDate: LocalDate?,
    memberList: List<StudyMember>,
    isLoading: Boolean = false,
    onClick: () -> Unit
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
            Text(study.title, style = AppTextStyle.BodyLarge.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(Dimens.Tiny))
            Text(study.description, style = AppTextStyle.Body.copy(color = DarkGray))
            Spacer(modifier = Modifier.height(Dimens.Tiny))
            StudyMetaInfoRow(
                createdAt = study.createdAt,
                memberCount = memberList.size,
                activeDays = study.activeDays,
                selectedDate = selectedDate
            )
            Spacer(modifier = Modifier.height(Dimens.Tiny))
            if(memberList.find { it.uid ==  uid} != null){
                Button(
                    enabled = false,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkGray,
                        contentColor = White
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {

                    },
                ) {
                    Text("참여중", style = AppTextStyle.BodySmallMedium.copy(color = White))
                }
            }
            else {
                Button(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = White
                        )
                    } else {
                        Text("참여하기", style = AppTextStyle.BodySmallMedium.copy(color = White))
                    }
                }
            }
        }
    }
}