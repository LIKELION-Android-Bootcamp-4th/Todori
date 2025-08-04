package com.mukmuk.todori.data.dummy

import com.mukmuk.todori.data.remote.quest.DailyUserQuest

object QuestDummy {
    val questSample = listOf(
        DailyUserQuest("1","오늘의 할 일 완료", 10, isCompleted = true),
        DailyUserQuest("2","타이머로 30분 이상 집중", 5, isCompleted = true),
        DailyUserQuest("3","로드맵 달성률 100%", 50, isCompleted = false),
        DailyUserQuest("4","스터디 게시글 작성", 15, isCompleted = false)
    )
}