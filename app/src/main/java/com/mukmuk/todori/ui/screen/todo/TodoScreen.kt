package com.mukmuk.todori.ui.screen.todo

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.mukmuk.todori.ui.screen.todo.component.TodoTopBar
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import java.time.YearMonth


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TodoScreen() {
    var selectedDate by remember {
        mutableStateOf(Clock.System.todayIn(TimeZone.currentSystemDefault()))
    }
    var selectedTabIndex by remember { mutableStateOf(0) }

    val tabs = listOf("개인", "목표", "스터디")

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TodoTopBar(selectedYearMonth = YearMonth.of(selectedDate.year, selectedDate.month)) { action ->
//            when (action) {
//                //todo : route 처리
//            }
        }


        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }

        when (selectedTabIndex) {
//            //todo : 탭 전환
//            0 -> PersonalTodoList()
//            1 -> GoalTodoList()
//            2 -> StudyTodoList()
        }
    }

}