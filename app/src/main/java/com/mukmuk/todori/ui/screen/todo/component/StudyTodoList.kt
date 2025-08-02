package com.mukmuk.todori.ui.screen.todo.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import com.google.firebase.Timestamp
import com.mukmuk.todori.data.remote.study.Study
import com.mukmuk.todori.data.remote.study.StudyMember
import com.mukmuk.todori.data.remote.study.StudyTodo
import com.mukmuk.todori.data.remote.study.TodoProgress
import com.mukmuk.todori.ui.screen.todo.component.card.StudyCard
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StudyTodoList() {
    val myUid = "myUid"
    val now = Date()

    // 스터디 샘플
    val studies = listOf(
        Study("1", "Compose 스터디", "Compose 앱 만들기", "Compose 완전정복", "uid1", Timestamp(now), listOf("월", "수", "금")),
        Study("2", "CS 스터디", "CS 기초 다지기", "네트워크/OS 복습", "uid2", Timestamp(Date(now.time - 3 * 86400000)), listOf("화", "목")),
        Study("3", "알고리즘 스터디", "PS 훈련소", "알고리즘 매일 풀이", "uid3", Timestamp(Date(now.time - 7 * 86400000)), listOf("월", "화", "수"))
    )

    // 내가 가입한 날짜
    val myMember = StudyMember(uid = myUid, nickname = "나", role = "MEMBER", joinedAt = Timestamp(Date(now.time - 5 * 86400000)))

    // Todo + 진행 상태
    val todosPerStudy = listOf(
        listOf(
            StudyTodo("1", "Jetpack Compose 학습", "uid1"),
            StudyTodo("2", "컴포즈 샘플 작성", "uid1")
        ),
        listOf(
            StudyTodo("3", "운영체제 정리", "uid2"),
            StudyTodo("4", "TCP/IP 복습", "uid2")
        ),
        listOf(
            StudyTodo("5", "BFS 문제 풀이", "uid3"),
            StudyTodo("6", "DP 복습", "uid3")
        )
    )

    val progressPerStudy = listOf(
        listOf(
            TodoProgress(uid = myUid, isDone = true),
            TodoProgress(uid = myUid, isDone = false)
        ),
        listOf(
            TodoProgress(uid = myUid, isDone = true),
            TodoProgress(uid = myUid, isDone = true)
        ),
        listOf(
            TodoProgress(uid = myUid, isDone = false),
            TodoProgress(uid = myUid, isDone = false)
        )
    )

    LazyColumn {
        items(studies.size) { index ->
            val study = studies[index]
            val todos = todosPerStudy[index]
            val progresses = progressPerStudy[index]
            val progressMap = todos.mapIndexed { i, todo ->
                todo.studyTodoId to progresses[i]
            }.toMap()

            StudyCard(
                study = study,
                studyTodos = todos,
                myProgressMap = progressMap,
                memberCount = 5,
                joinedAt = myMember.joinedAt!!,
                onClick = {
                    //todo : 상세 화면 연결
                }
            )
        }
    }
}
