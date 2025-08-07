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
    val progressMap = progresses.groupBy { it.uid }
        .mapValues { it.value.associateBy { p -> p.studyTodoId } }

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
                val todoProgresses = progressMap[member.uid] ?: emptyMap()
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
