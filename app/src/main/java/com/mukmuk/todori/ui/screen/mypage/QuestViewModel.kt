package com.mukmuk.todori.ui.screen.mypage

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

    //퀘스트 완료체크
    fun loadDailyQuests(uid: String) {
        viewModelScope.launch {
            // cloud function 호출
            val result = questRepository.callQuestCheckFunction(uid)
            _questCheckResult.value = result

            //퀘스트 목록 새로고침
            _dailyQuests.value = questRepository.getUserDailyQuests(uid)
        }
    }
}