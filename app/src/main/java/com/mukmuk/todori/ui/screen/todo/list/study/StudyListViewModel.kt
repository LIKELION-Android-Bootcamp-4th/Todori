package com.mukmuk.todori.ui.screen.todo.list.study

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mukmuk.todori.data.repository.StudyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class StudyListViewModel @Inject constructor(
    private val studyRepository: StudyRepository
) : ViewModel() {
    private val _state = MutableStateFlow(StudyListState())
    val state: StateFlow<StudyListState> = _state


    fun loadAllStudies(uid: String, date: String? = null) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val myStudies = studyRepository.getMyStudies(uid)
                val studyIds = myStudies.map { it.studyId }

                val studiesDeferred = async { studyRepository.getStudies(studyIds) }
                val membersDeferred = async { studyRepository.getMembersForStudies(studyIds) }
                val todosDeferred = async { studyRepository.getTodosForStudies(studyIds, date) }
                val progressesDeferred = async { studyRepository.getMyAllProgresses(uid) }

                val studies = studiesDeferred.await()
                val members = membersDeferred.await()
                val todos = todosDeferred.await()
                val progresses = progressesDeferred.await()

                val membersMap = members.groupBy { it.studyId }
                val todosMap = todos.groupBy { it.studyId }
                val progressMap = progresses.groupBy { it.studyId }
                    .mapValues { entry ->
                        entry.value.associateBy { it.studyTodoId }
                    }
                val studiesMap = studies.associateBy { it.studyId }


                _state.value = _state.value.copy(
                    myStudies = myStudies,
                    membersMap = membersMap,
                    studies = studiesMap,
                    todosMap = todosMap,
                    progressMap = progressMap,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message, isLoading = false)
            }
        }
    }

}
