package com.mukmuk.todori.data.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.mukmuk.todori.data.remote.goal.Goal
import kotlinx.coroutines.tasks.await

class GoalService(
    private val firestore: FirebaseFirestore
) {
    // 유저별 goals 컬렉션 참조
    private fun userGoalsRef(uid: String) =
        firestore.collection("users").document(uid).collection("goals")

    // 목표 생성
    suspend fun createGoal(uid: String, goal: Goal): String {
        val ref = userGoalsRef(uid).document() // auto-ID 생성
        val autoId = ref.id
        val goalWithId = goal.copy(goalId = autoId)
        ref.set(goalWithId, SetOptions.merge()).await()
        return autoId
    }

    // 목표 목록 조회
    suspend fun getGoals(uid: String): List<Goal> {
        val snapshot = userGoalsRef(uid).get().await()
        return snapshot.documents.mapNotNull { it.toObject(Goal::class.java) }
    }

    //목표 한 개 가져오기
    suspend fun getGoalById(uid: String, goalId: String): Goal? {
        val doc = userGoalsRef(uid).document(goalId).get().await()
        return doc.toObject(Goal::class.java)
    }

    // 목표 수정
    suspend fun updateGoal(uid: String, goal: Goal) {
        userGoalsRef(uid).document(goal.goalId).set(goal, SetOptions.merge()).await()
    }

    // 목표 삭제
    suspend fun deleteGoal(uid: String, goalId: String) {
        userGoalsRef(uid).document(goalId).delete().await()
    }
}
