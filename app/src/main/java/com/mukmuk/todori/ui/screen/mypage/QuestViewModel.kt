package com.mukmuk.todori.ui.screen.mypage

import android.util.Log
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
    val gainedPoint: Int = 0,
    val levelUp: Boolean = false,
    val level: Int = 1,
    val rewardPoint: Int = 0,     // 현 레벨 버킷 진행도
    val nextLevelPoint: Int = 0,  // 다음 레벨까지 남은 포인트
    val error: String? = null
)

@HiltViewModel
class QuestViewModel @Inject constructor(
    private val questRepository: QuestRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(QuestUiState())
    val ui: StateFlow<QuestUiState> = _ui

    /** 서버 1회 호출 → 실패 시 캐시 폴백 */
    fun loadDailyQuests(uid: String) {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = true, error = null)
            val res = questRepository.callUserQuest(uid)
            res.onSuccess { r ->
                val list = r.assigned.map {
                    DailyUserQuest(
                        questId = it.questId,
                        title = it.title,
                        points = it.points,
                        completed = it.completed
                    )
                }
                _ui.value = _ui.value.copy(
                    isLoading = false,
                    quests = list,
                    gainedPoint = r.reward.gainedPoint,
                    levelUp = r.reward.levelUp,
                    level = r.profile.level,
                    rewardPoint = r.profile.rewardPoint,
                    nextLevelPoint = r.profile.nextLevelPoint
                )
                Log.d("QuestViewModel", "✅ quests=${list.size}, lvl=${r.profile.level}, cur=${r.profile.rewardPoint}, nextRemain=${r.profile.nextLevelPoint}")
            }.onFailure { e ->
                val cached = questRepository.getCachedDailyQuests(uid)
                _ui.value = _ui.value.copy(
                    isLoading = false,
                    quests = cached,
                    error = e.message ?: "퀘스트 로딩 실패(오프라인/서버 오류)"
                )
                Log.e("QuestViewModel", "❌ loadDailyQuests: ${e.message}")
            }
        }
    }
}
