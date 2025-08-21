package com.mukmuk.todori.ui.screen.todo.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.data.remote.todo.Todo
import com.mukmuk.todori.data.remote.todo.TodoCategory
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoCategoryPickerBottomSheet (

    todoCategoryList: List<TodoCategory>,
    todosMap: Map<String, List<Todo>>,
    show: Boolean,
    onSelected: (TodoCategory) -> Unit,
    onDismissRequest: () -> Unit,

    ) {

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    if(show){
    ModalBottomSheet(
        modifier = Modifier.fillMaxHeight()
            .padding(top = 120.dp),
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = White
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "카테고리 선택",
                style = AppTextStyle.TitleMedium,
                modifier = Modifier.fillMaxWidth(),
            )

            LazyColumn(
                modifier = Modifier.padding(top = 16.dp)
            ) {
                items(todoCategoryList) { category ->
                    TodoCategoryListData(

                        categoryTitle = category.name,
                        description = category.description ?: "",
                        todos = todosMap[category.categoryId] ?: emptyList(),
                        onSelected = {
                            onSelected(category)
                        }


                    )
                }

            }

        }
    }

    }


}