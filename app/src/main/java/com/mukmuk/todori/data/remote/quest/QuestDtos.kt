package com.mukmuk.todori.data.remote.quest

data class QuestFunctionResponse(
    val assigned: List<AssignedQuestDTO>,
    val reward: RewardDTO,
    val profile: ProfileDTO,
    val meta: MetaDTO?
)

data class AssignedQuestDTO(
    val questId: String,
    val title: String,
    val completed: Boolean,
    val points: Int
)

data class RewardDTO(
    val gainedPoint: Int,
    val levelUp: Boolean,
    val newLevel: Int
)

data class ProfileDTO(
    val level: Int,
    val rewardPoint: Int,
    val nextLevelPoint: Int
)

data class MetaDTO(
    val today: String?,
    val joinedStudy: Boolean?
)