package com.mukmuk.todori.ui.screen.todo.component

import androidx.compose.foundation.border
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

import com.mukmuk.todori.data.remote.todo.Todo
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.White

@Composable
fun TodoCategoryListData(
    categoryTitle: String,
    description: String,
    todos: List<Todo>,
    onSelected: () -> Unit

) {

    Card (
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = Dimens.Large)
            .border(1.dp, Gray, RoundedCornerShape(10.dp)),
        onClick = {
            onSelected()
        },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = White
        )
    ){

        Column(

            modifier = Modifier.padding(Dimens.Medium)

        ) {

            Text(categoryTitle, style = AppTextStyle.BodyLarge.copy(fontWeight = FontWeight.Bold, color = Gray))

            Spacer(modifier = Modifier.height(Dimens.Tiny))

            Text(description, style = AppTextStyle.Body)

            Spacer(modifier = Modifier.height(Dimens.Tiny))

            todos.take(3).forEach { todo ->
                TodoItemRow(title = todo.title, isDone = todo.completed)
            }

        }

    }

}