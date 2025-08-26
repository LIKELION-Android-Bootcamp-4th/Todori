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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.mukmuk.todori.data.remote.study.StudyMember
import com.mukmuk.todori.ui.component.SimpleTopAppBar
import com.mukmuk.todori.ui.screen.todo.component.MemberProgressRow
import com.mukmuk.todori.ui.screen.todo.detail.study.StudyDetailViewModel
import com.mukmuk.todori.ui.theme.Dimens

@Composable
fun MemberProgressDetailScreen(
    navController: NavHostController,
    viewModel: StudyDetailViewModel
) {

    val state by viewModel.state.collectAsState()
    val members = state.members
    val todos = state.todos
    val progresses = state.progresses
    val usersById = state.usersById
    val progressMap = progresses.groupBy { it.uid }
        .mapValues { it.value.associateBy { p -> p.studyTodoId } }

    var query by remember { mutableStateOf("") }
    val filteredMembers = remember(query, members) {
        if (query.isBlank()) members
        else members.filter { it.nickname.contains(query, ignoreCase = true) }
    }

    val sortMembers = filteredMembers.map { member ->
        val todoProgresses = progressMap[member.uid] ?: emptyMap()
        val completedCount = todoProgresses.values.count { it.done }
        val totalCount = todos.size
        val progress = if (totalCount > 0) completedCount / totalCount.toFloat() else 0f

        Pair(member, progress)
    }.sortedWith(
        compareByDescending<Pair<StudyMember, Float>> { it.first.role == "LEADER" }
            .thenByDescending { it.second }
    )

    Column(Modifier.fillMaxSize().background(Color.White).padding(Dimens.Small)) {
        SimpleTopAppBar(
            title = "전체 멤버 ${members.size}명",
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
            items(sortMembers, key = { it.first.uid }) { (member, progress)  ->
                val todoProgresses = progressMap[member.uid] ?: emptyMap()
                val completedCount = todoProgresses.values.count { it.done }
                val level = usersById[member.uid]?.level ?: 1

                MemberProgressRow(
                    member = member,
                    completed = completedCount,
                    total = todos.size,
                    progress = progress,
                    level = level,
                    modifier = Modifier.fillMaxWidth().padding(Dimens.Tiny)
                )
            }
        }
    }
}
