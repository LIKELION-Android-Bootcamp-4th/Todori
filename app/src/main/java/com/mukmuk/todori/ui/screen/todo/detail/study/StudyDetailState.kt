package com.mukmuk.todori.ui.screen.todo.detail.study

import com.mukmuk.todori.data.remote.study.Study
import com.mukmuk.todori.data.remote.study.StudyMember
import com.mukmuk.todori.data.remote.study.StudyTodo
import com.mukmuk.todori.data.remote.study.TodoProgress
import com.mukmuk.todori.data.remote.user.User


data class StudyDetailState(
    val isLoading: Boolean = false,
    val study: Study? = null,
    val members: List<StudyMember> = emptyList(),
    val todos: List<StudyTodo> = emptyList(),
    val progresses: List<TodoProgress> = emptyList(),
    val usersById: Map<String, User> = emptyMap(),
    val error: String? = null,
    val studyDeleted: Boolean = false,
    val isDeleting: Boolean = false
)
