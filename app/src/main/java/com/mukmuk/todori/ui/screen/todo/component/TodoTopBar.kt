package com.mukmuk.todori.ui.screen.todo.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.mukmuk.todori.ui.theme.White
import java.time.YearMonth


enum class MenuAction {
    CreatePersonalCategory,
    CreateGoalRoadmap,
    CreateStudy,
    ShareTodo
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TodoTopBar(
    selectedYearMonth: YearMonth,
    onMenuClick: (MenuAction) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text("${selectedYearMonth.year}년 ${selectedYearMonth.monthValue}월")
        },
        actions = {
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "메뉴")
            }
            DropdownMenu(
                expanded = expanded,
                modifier = Modifier.background(White),
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(text = { Text("카테고리 생성") }, onClick = {
                    expanded = false
                    onMenuClick(MenuAction.CreatePersonalCategory)
                })
                DropdownMenuItem(text = { Text("목표 로드맵 생성") }, onClick = {
                    expanded = false
                    onMenuClick(MenuAction.CreateGoalRoadmap)
                })
                DropdownMenuItem(text = { Text("스터디 생성") }, onClick = {
                    expanded = false
                    onMenuClick(MenuAction.CreateStudy)
                })
                DropdownMenuItem(text = { Text("TODO 공유하기") }, onClick = {
                    expanded = false
                    onMenuClick(MenuAction.ShareTodo)
                })
            }
        }
    )
}