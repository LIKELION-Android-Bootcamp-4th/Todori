package com.mukmuk.todori.ui.screen.todo.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.UserPrimary
import com.mukmuk.todori.ui.theme.White

@Composable
fun TodoDetailHeader(
    categoryTitle: String,
    categorySubTitle: String,
    progress: Int,
    total: Int,
    newTodoText: String,
    onTodoTextChange: (String) -> Unit,
    onAddClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = White)
            .padding(Dimens.Medium)
    ) {
        Text(
            text = categoryTitle,
            style = AppTextStyle.Body.copy(fontWeight = FontWeight.Bold)
        )
        if (categorySubTitle.isNotBlank()) {
            Spacer(modifier = Modifier.height(Dimens.Nano))
            Text(
                text = categorySubTitle,
                style = AppTextStyle.BodySmall.copy(color = DarkGray)
            )
        }

        Spacer(modifier = Modifier.height(Dimens.Tiny))
        ProgressWithText(
            progress = progress / total.toFloat(),
            completed = progress,
            total = total,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.Medium)
        ) {
            OutlinedTextField(
                value = newTodoText,
                onValueChange = onTodoTextChange,
                placeholder = { Text("Todo 입력") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(Dimens.Small))
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(color = UserPrimary)
            ) {
                IconButton(
                    onClick = onAddClick,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "할 일 추가",
                        tint = White
                    )
                }
            }
        }
    }
}