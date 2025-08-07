package com.mukmuk.todori.ui.screen.todo.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.mukmuk.todori.data.remote.study.Study
import com.mukmuk.todori.data.remote.study.StudyMember
import com.mukmuk.todori.data.repository.StudyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class StudyViewModel @Inject constructor(
    private val repository: StudyRepository
) : ViewModel() {

    fun createStudy(
        title: String,
        description: String,
        activeDays: List<String>,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            // leaderId -> 내 UID (로그인 처리 후 변경), leaderNickname -> 내 닉네임 (로그인 처리 후 변경) -> viewModel에서 처리
            // val user = FirebaseAuth.getInstance().currentUser
            // val leaderId = user?.uid ?: throw Exception("로그인 필요")

            val leaderId = "testuser"
            val leaderNickname = "edittest"
            try {
                val now = Timestamp.now()
                // Study 객체 생성
                val study = Study(
                    studyName = title,
                    title = title,
                    description = description,
                    leaderId = leaderId,
                    createdAt = now,
                    activeDays = activeDays,
                    deleted = false,
                )

                // StudyMember 객체 생성
                val leaderMember = StudyMember(
                    uid = leaderId,
                    nickname = leaderNickname,
                    role = "LEADER",
                    joinedAt = now
                )

                repository.createStudy(study, leaderMember)

                withContext(Dispatchers.Main) { onSuccess() }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onError(e) }
            }
        }
    }
}
