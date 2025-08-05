package com.mukmuk.todori.ui.screen.todo.create

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.mukmuk.todori.data.remote.goal.Goal
import com.mukmuk.todori.data.repository.GoalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class GoalViewModel @Inject constructor(
    private val repository: GoalRepository
) : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    fun createGoal(
        title: String,
        description: String,
        startDate: LocalDate,
        endDate: LocalDate,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                //todo : 로그인 유저 정보 가져오기
//                val user = FirebaseAuth.getInstance().currentUser
//                val uid = user?.uid ?: throw Exception("로그인이 필요합니다.")
                val uid = "testuser"
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val goal = Goal(
                    uid = uid,
                    title = title,
                    description = description,
                    startDate = startDate.format(formatter),
                    endDate = endDate.format(formatter),
                    createdAt = Timestamp.now()
                )
                repository.createGoal(uid, goal)
                withContext(Dispatchers.Main) { onSuccess() }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onError(e) }
            }
        }
    }
}
