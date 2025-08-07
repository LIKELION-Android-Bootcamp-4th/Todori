package com.mukmuk.todori.ui.screen.todo.detail.study

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mukmuk.todori.data.repository.StudyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudyDetailViewModel @Inject constructor(
    private val studyRepository: StudyRepository
) : ViewModel() {

    private val _state = MutableStateFlow(StudyDetailState())
    val state: StateFlow<StudyDetailState> = _state

    fun loadStudyDetail(uid: String, studyId: String,date: String?) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val study = studyRepository.getStudies(listOf(studyId)).firstOrNull()
                val members = studyRepository.getMembersForStudies(listOf(studyId))
                val todos = studyRepository.getTodosForStudies(listOf(studyId), date)
                val progresses = if (date != null) {
                    studyRepository.getProgressesByStudyAndDate(studyId, date)
                } else {
                    studyRepository.getMyAllProgresses(uid).filter { it.studyId == studyId }
                }
                _state.value = _state.value.copy(
                    isLoading = false,
                    study = study,
                    members = members,
                    todos = todos,
                    progresses = progresses,
                    error = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun toggleTodoProgress(studyId: String, studyTodoId: String, uid: String, checked: Boolean) {
        val updatedProgresses = _state.value.progresses.map {
            if (it.studyTodoId == studyTodoId && it.uid == uid) it.copy(done = checked)
            else it
        }
        _state.value = _state.value.copy(progresses = updatedProgresses)

        viewModelScope.launch {
            try {
                studyRepository.toggleTodoProgressDone(studyId, studyTodoId, uid, checked)
            } catch (e: Exception) {
                val rolledBack = _state.value.progresses.map {
                    if (it.studyTodoId == studyTodoId && it.uid == uid) it.copy(done = !checked)
                    else it
                }
                _state.value = _state.value.copy(progresses = rolledBack, error = "저장 실패, 다시 시도해주세요.")
            }
        }
    }

}
