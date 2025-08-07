package com.mukmuk.todori.ui.screen.mypage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mukmuk.todori.data.remote.quest.DailyUserQuest
import com.mukmuk.todori.data.repository.QuestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class QuestViewModel @Inject constructor(
    private val questRepository: QuestRepository
) : ViewModel() {

    private val _dailyQuests = MutableStateFlow<List<DailyUserQuest>>(emptyList())
    val dailyQuests: StateFlow<List<DailyUserQuest>> = _dailyQuests

    private val _questCheckResult = MutableStateFlow<Result<String>?>(null)
    val questCheckResult: StateFlow<Result<String>?> = _questCheckResult

    // 퀘스트 완료 체크 및 불러오기
    fun loadDailyQuests(uid: String) {
        viewModelScope.launch {
            Log.d("QuestViewModel", "🔄 loadDailyQuests 호출 - uid 전달 확인: $uid")

            // Cloud Function 호출
            val result = questRepository.callQuestCheckFunction(uid)
            _questCheckResult.value = result

            result
                .onSuccess { Log.d("QuestViewModel", "✅ 함수 호출 성공: $it") }
                .onFailure { Log.e("QuestViewModel", "❌ 함수 호출 실패", it) }

            // Firestore에서 유저의 퀘스트 목록 불러오기
            val quests = questRepository.getUserDailyQuests(uid)
            Log.d("QuestViewModel", "📌 퀘스트 개수: ${quests.size}")
            _dailyQuests.value = quests
        }
    }
}
