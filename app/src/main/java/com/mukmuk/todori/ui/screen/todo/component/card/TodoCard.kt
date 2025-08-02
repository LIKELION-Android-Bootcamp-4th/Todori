package com.mukmuk.todori.ui.screen.todo.component.card

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.ui.screen.todo.component.CardHeaderSection
import com.mukmuk.todori.ui.screen.todo.component.ProgressWithText
import com.mukmuk.todori.ui.screen.todo.component.TodoItemRow
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Dimens.DefaultCornerRadius
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.White

@Composable
fun TodoCard(
    categoryTitle: String,
    subtitle: String,
    progress: Int,
    total: Int,
    todos: List<Pair<String, Boolean>>,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.Tiny)
            .clickable { onClick() },
        shape = RoundedCornerShape(DefaultCornerRadius),
        border = BorderStroke(1.dp, Gray),
        colors = CardDefaults.cardColors(
            containerColor = White
        )
    ) {
        Column(modifier = Modifier.padding(Dimens.Medium)) {
            // 상단 제목
            CardHeaderSection(title = categoryTitle, subtitle = subtitle)
            Spacer(modifier = Modifier.height(Dimens.Tiny))
            ProgressWithText(
                progress = progress / total.toFloat(),
                completed = progress,
                total = total,
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = Dimens.Nano
            )
            Spacer(modifier = Modifier.height(Dimens.Medium))

            todos.take(3).forEach { (task, isDone) ->
                TodoItemRow(title = task, isDone = isDone)
            }
        }
    }
}
