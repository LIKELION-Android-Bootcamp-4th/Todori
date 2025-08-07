package com.mukmuk.todori.ui.screen.todo.detail.study

import StudyTodoInputCard
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mukmuk.todori.ui.component.ProgressWithText
import com.mukmuk.todori.ui.screen.todo.component.CardHeaderSection
import com.mukmuk.todori.ui.screen.todo.component.CommonDetailAppBar
import com.mukmuk.todori.ui.screen.todo.component.MemberProgressCard
import com.mukmuk.todori.ui.screen.todo.component.StudyMetaInfoRow
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.GroupPrimary
import com.mukmuk.todori.ui.theme.White

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyDetailScreen(
    navController: NavHostController,
    studyId: String,
    selectedDate: String,
    onBack: () -> Unit
) {
    val uid = "testuser"
    val viewModel: StudyDetailViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()
    var newTodoText by remember { mutableStateOf("") }

    LaunchedEffect(studyId, selectedDate) {
        viewModel.loadStudyDetail(uid, studyId, selectedDate)
    }
    when {
        state.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        state.error != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("오류가 발생했습니다. 다시 시도해주세요.")
            }
        }

        state.study == null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("스터디 정보를 찾을 수 없습니다.", textAlign = TextAlign.Center)
            }
        }

        else -> {

            val study = state.study!!
            val members = state.members
            val todos = state.todos
            val progresses = state.progresses

            val myProgressMap = progresses.associateBy { it.studyTodoId }

            val completedCount = myProgressMap.values.count { it.uid == uid && it.done }
            val totalCount = todos.size
            val progress = if (totalCount > 0) completedCount / totalCount.toFloat() else 0f

            // 전체 멤버별 Map<uid, Map<todoId, TodoProgress>>
            val memberProgressMap = progresses.groupBy { it.uid }
                .mapValues { it.value.associateBy { p -> p.studyTodoId } }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    CommonDetailAppBar(
                        title = study.title,
                        onBack = onBack,
                        onEdit = {
                            navController.currentBackStackEntry
                                ?.savedStateHandle
                                ?.set("editStudy", study)
                            navController.navigate("study/create")
                        },
                        onDelete = {
                            //todo
                        }
                    )
                }
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(White)
                            .padding(Dimens.Small)
                    ) {
                        CardHeaderSection(
                            title = study.title,
                            subtitle = study.description,
                            showArrowIcon = false
                        )
                        Spacer(modifier = Modifier.height(Dimens.Small))
                        StudyMetaInfoRow(
                            createdAt = study.createdAt,
                            joinedAt = members.find { it.uid == uid }?.joinedAt ?: study.createdAt,
                            memberCount = members.size,
                            activeDays = study.activeDays
                        )
                        Spacer(modifier = Modifier.height(Dimens.Small))
                        ProgressWithText(
                            progress = progress,
                            completed = completedCount,
                            progressColor = GroupPrimary,
                            total = totalCount,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                item {
                    StudyTodoInputCard(
                        taskList = todos,
                        newTodoText = newTodoText,
                        onTodoTextChange = { newTodoText = it },
                        onAddClick = { /* TODO */ },
                        onToggleChecked = { todoId, checked -> /* TODO */ },
                        onDelete = { todoId -> /* TODO */ },
                        progressMap = myProgressMap
                    )
                }

                item {
                    MemberProgressCard(
                        members = members,
                        todos = todos,
                        progresses = memberProgressMap
                    ) {
                        navController.navigate("member_progress_detail/${study.studyId}")
                    }
                    Spacer(modifier = Modifier.height(Dimens.Small))
                }


            }
        }
    }
}
