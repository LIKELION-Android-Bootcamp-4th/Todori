package com.mukmuk.todori.ui.screen.todo.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.data.remote.todo.TodoCategory
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoPickerBottomSheet (

    todoCategoryList: List<TodoCategory>,
    onSelected: (TodoCategory) -> Unit,
    onDismiss: () -> Unit

) {

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
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
                items(todoCategoryList.size) {
                    TodoCategoryItem(
                        todoCategory = todoCategoryList[it],
                        onClick = {
                            onSelected(todoCategoryList[it])
                            onDismiss()
                        }
                    )
            }

        }

    }

}