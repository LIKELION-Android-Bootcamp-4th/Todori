package com.mukmuk.todori.ui.screen.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mukmuk.todori.data.remote.quest.DailyUserQuest
import com.mukmuk.todori.data.repository.QuestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuestUiState(
    val isLoading: Boolean = false,
    val quests: List<DailyUserQuest> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class QuestViewModel @Inject constructor(
    private val questRepository: QuestRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(QuestUiState())
    val ui: StateFlow<QuestUiState> = _ui

    fun loadDailyQuests(uid: String) {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = true, error = null)

            // 1) 캐시 먼저
            val cached = questRepository.getCachedDailyQuests(uid)
            if (cached.isNotEmpty()) {
                _ui.value = _ui.value.copy(isLoading = false, quests = cached)
            }

            // 2) 서버로 최신화 (퀘스트만 반영)
            val res = questRepository.refreshFromServer(uid)
            res.onSuccess { r ->
                val list = r.assigned.map {
                    DailyUserQuest(
                        questId = it.questId,
                        title = it.title,
                        points = it.points,
                        completed = it.completed
                    )
                }
                _ui.value = _ui.value.copy(isLoading = false, quests = list, error = null)
            }.onFailure { e ->
                _ui.value = _ui.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}
