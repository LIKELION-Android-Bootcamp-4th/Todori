package com.mukmuk.todori.ui.screen.todo.detail

import StudyTodoInputCard
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import com.google.firebase.Timestamp
import com.mukmuk.todori.data.remote.study.Study
import com.mukmuk.todori.data.remote.study.StudyMember
import com.mukmuk.todori.data.remote.study.StudyTodo
import com.mukmuk.todori.data.remote.study.TodoProgress
import com.mukmuk.todori.ui.screen.todo.component.CardHeaderSection
import com.mukmuk.todori.ui.screen.todo.component.MemberProgressCard
import com.mukmuk.todori.ui.screen.todo.component.ProgressWithText
import com.mukmuk.todori.ui.screen.todo.component.StudyMetaInfoRow
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.GroupPrimary
import com.mukmuk.todori.ui.theme.White
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyDetailScreen(
    navController: NavHostController,
    onBack: () -> Unit
) {
    val dummyStudy = Study(
        studyId = "study123",
        studyName = "스터디1",
        title = "Jetpack Compose 마스터하기",
        description = "함께 Jetpack Compose를 공부하는 스터디입니다.",
        leaderId = "user1",
        createdAt = Timestamp.now(),
        activeDays = listOf("월", "수", "금"),
        status = "ACTIVE"
    )
    val dummyMembers = listOf(
        StudyMember(uid = "user1", nickname = "코딩짱", role = "LEADER", joinedAt = Timestamp.now()),
        StudyMember(uid = "user2", nickname = "컴포즈왕", role = "MEMBER", joinedAt = Timestamp.now()),
        StudyMember(uid = "user3", nickname = "UI마스터", role = "MEMBER", joinedAt = Timestamp.now())
    )
    val todayTimestamp = Timestamp.now()
    val dummyTodos = listOf(
        StudyTodo(studyTodoId = "todo1", title = "Compose Layout 정리", createdBy = "user1", createdAt = todayTimestamp),
        StudyTodo(studyTodoId = "todo2", title = "기초 컴포넌트 만들기", createdBy = "user2", createdAt = todayTimestamp),
        StudyTodo(studyTodoId = "todo3", title = "디자인 시스템 적용", createdBy = "user3", createdAt = todayTimestamp)
    )
    val dummyProgressList = listOf(
        // todo1
        TodoProgress(studyTodoId = "todo1", uid = "user1", isDone = true, completedAt = todayTimestamp),
        TodoProgress(studyTodoId = "todo1", uid = "user2", isDone = true, completedAt = todayTimestamp),
        TodoProgress(studyTodoId = "todo1", uid = "user3", isDone = false, completedAt = null),
        // todo2
        TodoProgress(studyTodoId = "todo2", uid = "user1", isDone = false, completedAt = null),
        TodoProgress(studyTodoId = "todo2", uid = "user2", isDone = true, completedAt = todayTimestamp),
        TodoProgress(studyTodoId = "todo2", uid = "user3", isDone = false, completedAt = null),
        // todo3
        TodoProgress(studyTodoId = "todo3", uid = "user1", isDone = true, completedAt = todayTimestamp),
        TodoProgress(studyTodoId = "todo3", uid = "user2", isDone = false, completedAt = null),
        TodoProgress(studyTodoId = "todo3", uid = "user3", isDone = false, completedAt = null),
    )

    val currentUid = "user1"
    val myJoinedAt = dummyMembers.find { it.uid == currentUid }?.joinedAt

    val taskList = remember { mutableStateListOf(*dummyTodos.toTypedArray()) }
    var newTodoText by remember { mutableStateOf("") }

    val progressMap = remember {
        mutableStateMapOf<String, MutableMap<String, TodoProgress>>().apply {
            dummyMembers.forEach { member ->
                put(member.uid, mutableMapOf())
            }
            dummyProgressList.forEach { progress ->
                get(progress.uid)?.put(progress.studyTodoId, progress)
            }
        }
    }

    var dropdownExpanded by remember { mutableStateOf(false) }

    val myProgresses = progressMap[currentUid]?.values ?: emptyList()
    val completedCount = myProgresses.count { it.isDone }
    val totalCount = taskList.size
    val progress = if (totalCount > 0) completedCount / totalCount.toFloat() else 0f

    Column(modifier = Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(
            title = { Text("스터디") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(
                    onClick = { dropdownExpanded = true }
                ) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More")
                }
                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false },
                    modifier = Modifier.background(White)
                ) {
                    DropdownMenuItem(
                        text = { Text("수정", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                        onClick = {
                            dropdownExpanded = false
                            navController.currentBackStackEntry
                                ?.savedStateHandle
                                ?.set("editStudy", dummyStudy)
                            navController.navigate("study/create")
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("삭제", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                        onClick = {
                            dropdownExpanded = false
                            // TODO: 삭제
                        }
                    )
                }
            }
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(White)
                .padding(Dimens.Small)
        ) {
            CardHeaderSection(
                title = dummyStudy.title,
                subtitle = dummyStudy.description,
                showArrowIcon = false
            )
            Spacer(modifier = Modifier.height(Dimens.Small))
            StudyMetaInfoRow(
                createdAt = dummyStudy.createdAt,
                joinedAt = myJoinedAt,
                memberCount = dummyMembers.size,
                activeDays = dummyStudy.activeDays
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
        StudyTodoInputCard(
            taskList = taskList,
            newTodoText = newTodoText,
            onTodoTextChange = { newTodoText = it },
            onAddClick = {
                if (newTodoText.isNotBlank()) {
                    val newId = UUID.randomUUID().toString()
                    taskList.add(
                        StudyTodo(
                            studyTodoId = newId,
                            title = newTodoText.trim(),
                            createdBy = currentUid,
                            createdAt = Timestamp.now()
                        )
                    )
                    // 1️⃣ 반드시 새 객체 할당
                    dummyMembers.forEach { member ->
                        val oldMap = progressMap[member.uid]?.toMutableMap() ?: mutableMapOf()
                        oldMap[newId] = TodoProgress(
                            uid = member.uid,
                            studyTodoId = newId,
                            isDone = false,
                            completedAt = null
                        )
                        progressMap[member.uid] = oldMap
                    }
                    newTodoText = ""
                }
            },
            onToggleChecked = { todoId, checked ->
                val oldMap = progressMap[currentUid]?.toMutableMap() ?: mutableMapOf()
                val old = oldMap[todoId]
                oldMap[todoId] =
                    old?.copy(isDone = checked, completedAt = if (checked) Timestamp.now() else null)
                        ?: TodoProgress(
                            uid = currentUid,
                            studyTodoId = todoId,
                            isDone = checked,
                            completedAt = if (checked) Timestamp.now() else null
                        )
                progressMap[currentUid] = oldMap
            },
            onDelete = { todoId ->
                taskList.removeAll { it.studyTodoId == todoId }
                dummyMembers.forEach { member ->
                    val oldMap = progressMap[member.uid]?.toMutableMap() ?: mutableMapOf()
                    oldMap.remove(todoId)
                    progressMap[member.uid] = oldMap
                }
            },
            progressMap = progressMap[currentUid] ?: mutableMapOf(),
        )

        MemberProgressCard(
            members = dummyMembers,
            todos = taskList,
            progresses = progressMap
        ) { /* onClick */ }
    }
}
