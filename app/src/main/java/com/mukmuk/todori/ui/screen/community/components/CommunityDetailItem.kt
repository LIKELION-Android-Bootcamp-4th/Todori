package com.mukmuk.todori.ui.screen.community.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.mukmuk.todori.ui.screen.todo.component.StudyMetaInfoRow
import com.mukmuk.todori.ui.screen.todo.detail.study.StudyDetailViewModel
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.White

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CommunityDetailItem(
    studyId: String,
    onClick: () -> Unit
) {

    val viewModel: StudyDetailViewModel = hiltViewModel()

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        if(studyId != null) {
            viewModel.loadStudyDetail("testuser", studyId, null)
        }
    }

    val study: Study = state.study ?: return

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

            Text(state.study!!.description, style = AppTextStyle.Body.copy(color = DarkGray))

            Spacer(modifier = Modifier.height(Dimens.Tiny))

            val memberCount = state.members.size

            StudyMetaInfoRow(
                createdAt = state.study!!.createdAt,
                memberCount = memberCount,
                activeDays = state.study!!.activeDays
            )

            Spacer(modifier = Modifier.height(Dimens.Tiny))

            if(state.members.find { it.uid == "testuser" } != null){
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
                    Text("참여중", style = AppTextStyle.MypageButtonText.copy(color = White))
                }
            }
            else {
                Button(
                    onClick = {
                        viewModel.updateStudyMember(
                            studyId,
                            StudyMember(
                                uid = "testuser",
                                nickname = "testuser",
                                studyId = studyId,
                                role = "MEMBER",
                                joinedAt = Timestamp.now(),
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("참여하기", style = AppTextStyle.MypageButtonText.copy(color = White))
                }
            }
        }
    }

}