package com.mukmuk.todori.ui.screen.todo.component

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.mukmuk.todori.ui.screen.todo.component.card.TodoCard
import kotlinx.datetime.LocalDate

@Composable
fun TodoList(selectedDate: LocalDate) {
    val cardList = listOf(
        Pair("운동", "몸짱이 될거야 ~~~~~~~~~"),
        Pair("공부", "오늘도 열공한다~"),
        Pair("명상", "마음의 평화...")
    )
    val todos = listOf(
        listOf(
            "필기 교재 1회독" to false,
            "필기 교재 1회독" to true,
            "필기 교재 1회독" to true,
        ),
        listOf(
            "수학 문제 10개" to true,
            "단어 암기" to true,
            "리딩 지문 해석" to false,
        ),
        listOf(
            "10분 호흡 명상" to true,
            "자기 전에 감사 일기" to false,
            "차분한 음악 듣기" to false
        )
    )

    LazyColumn {
        items(cardList.indices.toList()) { index ->
            val (categoryTitle, categorySubTitle) = cardList[index]
            val taskList = todos[index]
            val total = taskList.size
            val progress = taskList.count { it.second }

            TodoCard(
                categoryTitle = categoryTitle,
                subtitle = categorySubTitle,
                progress = progress,
                total = total,
                todos = taskList
            ) {
                //TODO : 상세 화면 이동
            }
        }
    }
}
