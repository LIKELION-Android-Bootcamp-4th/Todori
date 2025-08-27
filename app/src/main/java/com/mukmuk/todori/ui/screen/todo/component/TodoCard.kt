package com.mukmuk.todori.ui.screen.todo.component

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.data.remote.todo.TodoCategory
import com.mukmuk.todori.ui.component.ProgressWithText
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.ButtonPrimary
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Dimens.DefaultCornerRadius
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.White

@Composable
fun TodoCard(
    categoryTitle: String,
    category: TodoCategory? = null,
    userName: String? = null,
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                if(category != null){
                    Box(
                        modifier = Modifier
                            .background(
                                color = ButtonPrimary,
                                shape = RoundedCornerShape(30)
                            )
                            .padding(Dimens.Tiny)
                    ){
                        Text("${userName}의 카테고리", style = AppTextStyle.Body.copy(color = White))
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.Tiny))

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
