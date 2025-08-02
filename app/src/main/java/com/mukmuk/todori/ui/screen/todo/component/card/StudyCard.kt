package com.mukmuk.todori.ui.screen.todo.component.card

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.PeopleAlt
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.mukmuk.todori.data.remote.study.Study
import com.mukmuk.todori.data.remote.study.StudyTodo
import com.mukmuk.todori.data.remote.study.TodoProgress
import com.mukmuk.todori.ui.component.CustomLinearProgressBar
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Dimens.DefaultCornerRadius
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.UserPrimary
import com.mukmuk.todori.ui.theme.White
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StudyCard(
    study: Study,
    studyTodos: List<StudyTodo>,
    myProgressMap: Map<String, TodoProgress>,
    memberCount: Int,
    joinedAt: Timestamp,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy. MM. dd")
    val createdDate = remember(study.createdAt) {
        study.createdAt?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
    }
    val joinedDate = remember(joinedAt) {
        joinedAt.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    }
    val today = remember { LocalDate.now() }
    val dDay = ChronoUnit.DAYS.between(joinedDate, today).toInt()

    val completed = studyTodos.count { myProgressMap[it.studyTodoId]?.isDone == true }
    val total = studyTodos.size
    val progress = if (total > 0) completed / total.toFloat() else 0f

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimens.Tiny)
            .clickable { onClick() },
        shape = RoundedCornerShape(DefaultCornerRadius),
        border = BorderStroke(1.dp, Gray),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Column(modifier = Modifier.padding(Dimens.Medium)) {

            // 제목 & 설명
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(study.studyName, style = AppTextStyle.Body.copy(fontWeight = FontWeight.Bold))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
            }

            Spacer(modifier = Modifier.height(Dimens.Nano))
            Text(study.description, style = AppTextStyle.BodySmall.copy(color = DarkGray))
            Spacer(modifier = Modifier.height(Dimens.Tiny))
            // 스터디 정보
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                        .background(UserPrimary)
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
                        .padding(Dimens.Nano)
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
                        .padding(Dimens.Nano)
                ) {
                    Icon(Icons.Default.Repeat, contentDescription = null,modifier = Modifier.size(16.dp), tint = Black)
                    Spacer(modifier = Modifier.width(Dimens.Nano))
                    Text(
                        text = study.activeDays.joinToString(" "),
                        style = AppTextStyle.BodySmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

            }

            Spacer(modifier = Modifier.height(Dimens.Tiny))

            // 진행률
            Text("진행률 $completed / $total", style = AppTextStyle.BodySmall.copy(fontWeight = FontWeight.Bold, color = DarkGray))
            Spacer(modifier = Modifier.height(Dimens.Tiny))
            CustomLinearProgressBar(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.Tiny),
                cornerRadius = Dimens.Nano
            )

            Spacer(modifier = Modifier.height(Dimens.Medium))

            studyTodos.take(3).forEach { todo ->
                val isDone = myProgressMap[todo.studyTodoId]?.isDone == true
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (isDone) UserPrimary else Gray
                    )
                    Spacer(modifier = Modifier.width(Dimens.Tiny))
                    Text(
                        text = todo.title,
                        style = AppTextStyle.BodySmall.copy(
                            color = if (isDone) DarkGray else Black,
                            textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None
                        )
                    )
                }
            }
        }
    }
}
