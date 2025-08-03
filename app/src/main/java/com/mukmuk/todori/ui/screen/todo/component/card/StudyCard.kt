package com.mukmuk.todori.ui.screen.todo.component.card

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.mukmuk.todori.data.remote.study.Study
import com.mukmuk.todori.data.remote.study.StudyTodo
import com.mukmuk.todori.data.remote.study.TodoProgress
import com.mukmuk.todori.ui.screen.todo.component.CardHeaderSection
import com.mukmuk.todori.ui.screen.todo.component.ProgressWithText
import com.mukmuk.todori.ui.screen.todo.component.StudyMetaInfoRow
import com.mukmuk.todori.ui.screen.todo.component.TodoItemRow
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
            CardHeaderSection(title = study.title, subtitle = study.description)
            Spacer(modifier = Modifier.height(Dimens.Tiny))
            // 스터디 정보
            StudyMetaInfoRow(
                createdAt = study.createdAt,
                joinedAt = joinedAt,
                memberCount = memberCount,
                activeDays = study.activeDays
            )


            Spacer(modifier = Modifier.height(Dimens.Tiny))

            ProgressWithText(
                progress = progress,
                completed = completed,
                total = total,
                progressColor = GroupPrimary,
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = Dimens.Nano
            )

            Spacer(modifier = Modifier.height(Dimens.Medium))

            studyTodos.take(3).forEach { todo ->
                val isDone = myProgressMap[todo.studyTodoId]?.isDone == true
                TodoItemRow(title = todo.title, isDone = isDone)
            }
        }
    }
}
