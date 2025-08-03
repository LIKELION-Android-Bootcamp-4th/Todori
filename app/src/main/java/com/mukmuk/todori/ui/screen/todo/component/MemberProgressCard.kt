package com.mukmuk.todori.ui.screen.todo.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.data.remote.study.StudyMember
import com.mukmuk.todori.data.remote.study.StudyTodo
import com.mukmuk.todori.data.remote.study.TodoProgress
import com.mukmuk.todori.ui.component.CustomLinearProgressBar
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.GroupPrimary
import com.mukmuk.todori.ui.theme.White

@Composable
fun MemberProgressCard(
    members: List<StudyMember>,
    todos: List<StudyTodo>,
    progresses: Map<String, Map<String, TodoProgress>>,
    onClick: () -> Unit
) {
    val memberProgressList = members.map { member ->
        val todoProgresses = progresses[member.uid] ?: emptyMap()
        val completedCount = todoProgresses.values.count { it.isDone }
        val totalCount = todos.size
        val progress = if (totalCount > 0) completedCount / totalCount.toFloat() else 0f
        Triple(member, completedCount, progress)
    }.sortedByDescending { it.third }.take(3)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.Small),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.dp, DarkGray)
    ) {
        Column(Modifier.padding(Dimens.Small)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("멤버 진행률", style = AppTextStyle.Body)
                }
                IconButton(
                    onClick = {
                        onClick()
                    }
                ) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "자세히 보기")
                }
            }

            Spacer(modifier = Modifier.height(Dimens.Small))

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(memberProgressList) { (member, completed, progress) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 사용자 이미지 (임시 아이콘으로 대체)
                        Icon(Icons.Default.AccountCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (member.role == "LEADER") {
                                    Icon(Icons.Default.Star, tint = Color.Yellow, contentDescription = "Leader", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                }
                                Text(member.nickname, style = AppTextStyle.Body.copy(fontWeight = FontWeight.Bold))
                            }

                            Text("한 줄 소개 부분입니다.", style = AppTextStyle.BodySmall)

                            CustomLinearProgressBar(
                                progress = progress,
                                progressColor = GroupPrimary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                trackColor = Gray
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                        Text("$completed/${todos.size}", style = AppTextStyle.BodySmall)
                    }
                }
            }
        }
    }
}
