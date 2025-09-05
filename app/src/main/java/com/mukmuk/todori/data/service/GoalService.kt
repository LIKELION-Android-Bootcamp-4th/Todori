package com.mukmuk.todori.data.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.mukmuk.todori.data.remote.goal.Goal
import kotlinx.coroutines.tasks.await

class GoalService(
    private val firestore: FirebaseFirestore
) {
    private fun userGoalsRef(uid: String) =
        firestore.collection("users").document(uid).collection("goals")

    suspend fun createGoal(uid: String, goal: Goal): String {
        val ref = userGoalsRef(uid).document()
        val autoId = ref.id
        val goalWithId = goal.copy(goalId = autoId)
        ref.set(goalWithId, SetOptions.merge()).await()
        return autoId
    }

    suspend fun getGoals(uid: String): List<Goal> {
        val snapshot = userGoalsRef(uid).get().await()
        return snapshot.documents.mapNotNull { it.toObject(Goal::class.java) }
    }

    suspend fun getGoalById(uid: String, goalId: String): Goal? {
        val doc = userGoalsRef(uid).document(goalId).get().await()
        return doc.toObject(Goal::class.java)
    }

    suspend fun updateGoal(uid: String, goal: Goal) {
        userGoalsRef(uid).document(goal.goalId).set(goal, SetOptions.merge()).await()
    }

    suspend fun deleteGoal(uid: String, goalId: String) {
        userGoalsRef(uid).document(goalId).delete().await()
    }
}
