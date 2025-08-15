package com.mukmuk.todori.ui.screen.mypage.studytargets

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mukmuk.todori.data.remote.user.StudyTargets
import com.mukmuk.todori.data.repository.StudyTargetsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudyTargetsViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val studyTargetsRepository: StudyTargetsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(StudyTargetsState())
    val state: StateFlow<StudyTargetsState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<StudyTargetsEffect>()
    val effect: SharedFlow<StudyTargetsEffect> = _effect.asSharedFlow()

    init {
        loadStudyTargets()
    }

    fun loadStudyTargets() {
        val uid = auth.currentUser?.uid ?: return

        updateState { copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val targets = studyTargetsRepository.getStudyTargets(uid) ?: StudyTargets()
                updateState {
                    copy(
                        targets = targets,
                        isLoading = false,
                        error = null
                    )
                }
                checkConsistency()
            } catch (e: Exception) {
                Log.e("StudyTargetsViewModel", "목표 로딩 실패", e)
                updateState {
                    copy(
                        isLoading = false,
                        error = "목표를 불러올 수 없어요: ${e.message}"
                    )
                }
            }
        }
    }

    fun updateDailyTarget(minutes: Int) {
        updateState {
            copy(
                targets = targets.copy(dailyMinutes = minutes),
                successMessage = null,
                error = null
            )
        }
        checkConsistency()
        saveTargets()
    }

    fun updateWeeklyTarget(minutes: Int) {
        updateState {
            copy(
                targets = targets.copy(weeklyMinutes = minutes),
                successMessage = null,
                error = null
            )
        }
        checkConsistency()
        saveTargets()
    }

    fun updateMonthlyTarget(minutes: Int) {
        updateState {
            copy(
                targets = targets.copy(monthlyMinutes = minutes),
                successMessage = null,
                error = null
            )
        }
        checkConsistency()
        saveTargets()
    }

    fun updateAllTargets(dailyMinutes: Int, weeklyMinutes: Int, monthlyMinutes: Int) {
        updateState {
            copy(
                targets = targets.copy(
                    dailyMinutes = dailyMinutes,
                    weeklyMinutes = weeklyMinutes,
                    monthlyMinutes = monthlyMinutes
                ),
                successMessage = null,
                error = null
            )
        }
        checkConsistency()
        saveTargets()
    }

    fun checkTempConsistency(dailyMinutes: Int, weeklyMinutes: Int, monthlyMinutes: Int) {
        val tempTargets = StudyTargets(
            dailyMinutes = dailyMinutes.takeIf { it > 0 },
            weeklyMinutes = weeklyMinutes.takeIf { it > 0 },
            monthlyMinutes = monthlyMinutes.takeIf { it > 0 }
        )
        val consistency = studyTargetsRepository.validateTargetConsistency(tempTargets)
        updateState { copy(tempConsistency = consistency) }
    }

    fun updateDailyTargetFromText(text: String) {
        val minutes = text.toIntOrNull()
        if (minutes != null && minutes >= 0) {
            updateDailyTarget(minutes)
        }
    }

    fun updateWeeklyTargetFromText(text: String) {
        val minutes = text.toIntOrNull()
        if (minutes != null && minutes >= 0) {
            updateWeeklyTarget(minutes)
        }
    }

    fun updateMonthlyTargetFromText(text: String) {
        val minutes = text.toIntOrNull()
        if (minutes != null && minutes >= 0) {
            updateMonthlyTarget(minutes)
        }
    }

    fun toggleConsistencyCheck() {
        updateState { copy(showConsistencyCheck = !showConsistencyCheck) }
    }

    fun applySuggestion(type: TargetType, suggestedMinutes: Int) {
        when (type) {
            TargetType.DAILY -> updateDailyTarget(suggestedMinutes)
            TargetType.WEEKLY -> updateWeeklyTarget(suggestedMinutes)
            TargetType.MONTHLY -> updateMonthlyTarget(suggestedMinutes)
        }
    }

    fun clearMessages() {
        updateState { copy(error = null, successMessage = null) }
    }

    private fun saveTargets() {
        val uid = auth.currentUser?.uid ?: return

        updateState { copy(isSaving = true) }

        viewModelScope.launch {
            try {
                studyTargetsRepository.updateStudyTargets(uid, _state.value.targets)
                updateState {
                    copy(
                        isSaving = false,
                        successMessage = "목표가 저장되었어요!"
                    )
                }
                _effect.emit(StudyTargetsEffect.SaveSuccess)

                updateState { copy(successMessage = null) }
            } catch (e: Exception) {
                Log.e("StudyTargetsViewModel", "목표 저장 실패", e)
                updateState {
                    copy(
                        isSaving = false,
                        error = "저장에 실패했어요: ${e.message}"
                    )
                }
            }
        }
    }

    private fun checkConsistency() {
        val consistency = studyTargetsRepository.validateTargetConsistency(_state.value.targets)
        updateState { copy(consistency = consistency) }
    }

    private fun updateState(update: StudyTargetsState.() -> StudyTargetsState) {
        _state.value = _state.value.update()
    }
}

sealed class StudyTargetsEffect {
    object SaveSuccess : StudyTargetsEffect()
    data class ShowMessage(val message: String) : StudyTargetsEffect()
}

enum class TargetType {
    DAILY, WEEKLY, MONTHLY
}