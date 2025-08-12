package com.mukmuk.todori.ui.screen.todo.create

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
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
                val uid = Firebase.auth.currentUser?.uid.toString()
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

    fun updateGoal(
        uid: String,
        goal: Goal,
        onSuccess: () -> Unit = {},
        onError: (Throwable) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                repository.updateGoal(uid, goal)
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
}
