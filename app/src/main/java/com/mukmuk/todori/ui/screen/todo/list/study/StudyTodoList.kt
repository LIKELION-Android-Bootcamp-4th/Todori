package com.mukmuk.todori.ui.screen.todo.list.study

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.firebase.Timestamp
import com.mukmuk.todori.ui.screen.todo.component.StudyCard
import kotlinx.datetime.LocalDate


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StudyTodoList(selectedDate: LocalDate,navController: NavHostController) {
    val viewModel: StudyListViewModel = hiltViewModel()
    val uid = "testuser"

    Log.d("TODORI", "selectedDate = $selectedDate")

    val state by viewModel.state.collectAsState()

    LaunchedEffect(selectedDate) {
        viewModel.loadAllStudies(uid, selectedDate.toString())
    }
    val studies = state.studies.values.toList()
    val membersMap = state.membersMap
    val todosMap = state.todosMap
    val myProgressMap = state.progressMap


    LazyColumn {
        items(studies.size) { index ->
            val study = studies[index]
            val members = membersMap[study.studyId].orEmpty()
            val todos = todosMap[study.studyId].orEmpty()
            val progresses = myProgressMap[study.studyId].orEmpty()
            StudyCard(
                study = study,
                studyTodos =  todos,
                myProgressMap = progresses,
                memberCount = members.size,
                joinedAt = members.firstOrNull { it.uid == uid }?.joinedAt ?: Timestamp.now(),
                onClick = {
                    navController.navigate("study/detail/${study.studyId}")
                }
            )
        }
    }
}
