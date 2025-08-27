package com.mukmuk.todori.ui.screen.todo.detail.study

import StudyTodoInputCard
import android.os.Build
import android.widget.Toast
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.mukmuk.todori.ui.component.ProgressWithText
import com.mukmuk.todori.ui.screen.todo.component.CardHeaderSection
import com.mukmuk.todori.ui.screen.todo.component.CommonDetailAppBar
import com.mukmuk.todori.ui.screen.todo.component.MemberProgressCard
import com.mukmuk.todori.ui.screen.todo.component.StudyMetaInfoRow
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.GroupPrimary
import com.mukmuk.todori.ui.theme.Red
import com.mukmuk.todori.ui.theme.White
import kotlinx.datetime.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyDetailScreen(
    navController: NavHostController,
    studyId: String,
    selectedDate: String,
    onBack: () -> Unit
) {
    val uid = Firebase.auth.currentUser?.uid.toString()
    val viewModel: StudyDetailViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()
    var newTodoText by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val myMember = state.members.find { it.uid == uid }
    val isLeader = myMember?.role == "LEADER"
    val parsedDate = remember(selectedDate) { LocalDate.parse(selectedDate) }
    val focusManager = LocalFocusManager.current
    if (state.studyDeleted) {
        LaunchedEffect(Unit) {
            onBack()
            viewModel.resetCategoryDeleted()
        }
    }

    if (state.isDeleting) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("삭제 중입니다...") },
            text = { Text("스터디와 관련된 모든 데이터가 삭제되고 있습니다.\n\n잠시만 기다려주세요.") },
            confirmButton = {},
            containerColor = White
        )
    }


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

            val myProgressMap = progresses
                .filter { it.uid == uid }
                .associateBy { it.studyTodoId }


            val completedCount = myProgressMap.values.count { it.uid == uid && it.done }
            val totalCount = todos.size
            val progress = if (totalCount > 0) completedCount / totalCount.toFloat() else 0f

            // 전체 멤버별 Map<uid, Map<todoId, TodoProgress>>
            val memberProgressMap = progresses.groupBy { it.uid }
                .mapValues { it.value.associateBy { p -> p.studyTodoId } }

            // 삭제 다이얼로그
            if (showDeleteDialog) {
                AlertDialog(
                    containerColor = White,
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("스터디 삭제") },
                    text = { Text("이 스터디와 관련된 모든 데이터가 함께 삭제됩니다. 정말 진행할까요?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDeleteDialog = false
                                viewModel.deleteStudyWithAllData(
                                    study.studyId,
                                    onSuccess = {
                                        navController.popBackStack()
                                    },
                                    onError = { msg ->
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        ) { Text("삭제", style = AppTextStyle.Body.copy(color = Red)) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("취소", style = AppTextStyle.Body.copy(color = Black))
                        }
                    }
                )
            }
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
                            showDeleteDialog = true // 다이얼로그 열기
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
                            activeDays = study.activeDays,
                            selectedDate = parsedDate
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
                        onAddClick = {
                            if (newTodoText.isNotBlank()) {
                                viewModel.addStudyTodo(
                                    study = study,
                                    title = newTodoText,
                                    date = selectedDate,
                                    createdBy = uid,
                                    members = members
                                )
                                newTodoText = ""
                                focusManager.clearFocus()
                            }
                        },
                        onToggleChecked = { todoId, checked ->
                            viewModel.toggleTodoProgress(
                                studyId = study.studyId,
                                studyTodoId = todoId,
                                uid = uid,
                                checked = checked
                            )
                        },
                        onDelete = { todoId ->
                            viewModel.deleteStudyTodo(todoId)
                        },
                        progressMap = myProgressMap,
                        isLeader = isLeader
                    )
                }

                item {
                    val updatedMembers = state.members.map { member ->
                        if (member.uid == uid) {
                            member.copy(nickname = state.usersById[uid]?.nickname ?: member.nickname)
                        } else {
                            member
                        }
                    }
                    MemberProgressCard(
                        members = updatedMembers,
                        todos = todos,
                        progresses = memberProgressMap,
                        usersById = state.usersById,
                    ) {
                        navController.navigate("member_progress_detail/${study.studyId}?date=$selectedDate")
                    }
                    Spacer(modifier = Modifier.height(Dimens.Small))
                }


            }
        }
    }
}
