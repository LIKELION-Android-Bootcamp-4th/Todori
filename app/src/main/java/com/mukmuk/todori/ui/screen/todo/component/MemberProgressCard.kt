package com.mukmuk.todori.ui.screen.todo.component

import android.R.attr.level
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.PeopleOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.data.remote.study.StudyMember
import com.mukmuk.todori.data.remote.study.StudyTodo
import com.mukmuk.todori.data.remote.study.TodoProgress
import com.mukmuk.todori.data.remote.user.User
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.White

@Composable
fun MemberProgressCard(
    members: List<StudyMember>,
    todos: List<StudyTodo>,
    progresses: Map<String, Map<String, TodoProgress>>,
    usersById: Map<String, User>,
    onClick: () -> Unit
) {
    val memberProgressList = members.map { member ->
        val todoProgresses = progresses[member.uid] ?: emptyMap()
        val completedCount = todoProgresses.values.count { it.done }
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
                    Icon(Icons.Outlined.PeopleOutline, contentDescription = null)
                    Spacer(modifier = Modifier.width(Dimens.Nano))
                    Text("멤버 진행률", style = AppTextStyle.Body.copy(fontWeight = FontWeight.Bold))
                }
                IconButton(
                    onClick = {
                        onClick()
                    }
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "자세히 보기")
                }
            }

            Spacer(modifier = Modifier.height(Dimens.Small))

            Column(modifier = Modifier.fillMaxWidth()) {
                memberProgressList.forEach { (member, completed, progress) ->
                    val level = usersById[member.uid]?.level ?: 1
                    MemberProgressRow(
                        member = member,
                        completed = completed,
                        total = todos.size,
                        progress = progress,
                        level = level,
                    )
                }
            }
        }
    }
}
