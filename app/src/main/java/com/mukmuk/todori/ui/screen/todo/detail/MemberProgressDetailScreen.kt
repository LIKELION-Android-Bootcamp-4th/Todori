package com.mukmuk.todori.ui.screen.todo.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.mukmuk.todori.data.remote.study.StudyMember
import com.mukmuk.todori.data.remote.study.StudyTodo
import com.mukmuk.todori.data.remote.study.TodoProgress
import com.mukmuk.todori.ui.component.SimpleTopAppBar
import com.mukmuk.todori.ui.screen.todo.component.MemberProgressRow
import com.mukmuk.todori.ui.theme.Dimens

@Composable
fun MemberProgressDetailScreen(
    navController: NavHostController,
    studyId: String,
) {
    //dummyData
    val members = listOf(
        StudyMember("user1", "코딩짱", "LEADER", joinedAt = /* Timestamp.now() */ null),
        StudyMember("user2", "UI마스터", "MEMBER", joinedAt = null),
        StudyMember("user3", "자료구조장인", "MEMBER", joinedAt = null),
        StudyMember("user4", "디버깅요정", "MEMBER", joinedAt = null),
        StudyMember("user5", "Android신", "MEMBER", joinedAt = null),
        StudyMember("user6", "테스트장인", "MEMBER", joinedAt = null),
        StudyMember("user7", "PullRequest러버", "MEMBER", joinedAt = null),
        StudyMember("user8", "알고리즘귀신", "MEMBER", joinedAt = null),
    )
    val todos = listOf(
        StudyTodo("todo1", "Compose 정리", "user1", createdAt = null),
        StudyTodo("todo2", "MVVM 복습", "user2", createdAt = null),
        StudyTodo("todo3", "Navigation 구현", "user3", createdAt = null),
        StudyTodo("todo4", "Firebase 연동", "user4", createdAt = null),
        StudyTodo("todo5", "UI 테스트", "user5", createdAt = null),
    )

    val progresses = members.associate { member ->
        member.uid to todos.associate { todo ->
            todo.studyTodoId to TodoProgress(
                studyTodoId = todo.studyTodoId,
                uid = member.uid,
                done = (0..1).random() == 1,
                completedAt = null
            )
        }.toMutableMap()
    }

    var query by remember { mutableStateOf("") }
    val filteredMembers = remember(query, members) {
        if (query.isBlank()) members
        else members.filter { it.nickname.contains(query, ignoreCase = true) }
    }


    Column(Modifier.fillMaxSize().background(Color.White).padding(Dimens.Small)) {
        SimpleTopAppBar(
            title = "멤버 ${members.size}",
            onBackClick = {
                navController.popBackStack()
            }
        )

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("멤버 이름 검색") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Dimens.Tiny)
        )

        LazyColumn(
            Modifier.fillMaxSize().padding(Dimens.Tiny)
        ) {
            items(filteredMembers) { member ->
                val todoProgresses = progresses[member.uid] ?: emptyMap()
                val completedCount = todoProgresses.values.count { it.done }
                val totalCount = todos.size
                val progress = if (totalCount > 0) completedCount / totalCount.toFloat() else 0f

                MemberProgressRow(
                    member = member,
                    completed = completedCount,
                    total = todos.size,
                    progress = progress,
                    modifier = Modifier.fillMaxWidth().padding(Dimens.Tiny)
                )
            }
        }
    }
}
