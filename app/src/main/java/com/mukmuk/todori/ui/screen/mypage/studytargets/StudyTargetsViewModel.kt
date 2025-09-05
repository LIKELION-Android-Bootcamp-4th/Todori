package com.mukmuk.todori.ui.screen.mypage.studytargets

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
            } catch (e: Exception) {
                updateState {
                    copy(
                        isLoading = false,
                        error = "목표를 불러올 수 없어요: ${e.message}"
                    )
                }
            }
        }
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
        saveTargets()
    }

    fun toggleConsistencyCheck() {
        updateState { copy(showConsistencyCheck = !showConsistencyCheck) }
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
                updateState {
                    copy(
                        isSaving = false,
                        error = "저장에 실패했어요: ${e.message}"
                    )
                }
            }
        }
    }


    private fun updateState(update: StudyTargetsState.() -> StudyTargetsState) {
        _state.value = _state.value.update()
    }
}

sealed class StudyTargetsEffect {
    object SaveSuccess : StudyTargetsEffect()
    data class ShowMessage(val message: String) : StudyTargetsEffect()
}