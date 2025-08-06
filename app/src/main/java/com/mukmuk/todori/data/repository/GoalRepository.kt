package com.mukmuk.todori.data.repository

import com.mukmuk.todori.data.remote.goal.Goal
import com.mukmuk.todori.data.service.GoalService
import javax.inject.Inject

class GoalRepository @Inject constructor(
    private val goalService: GoalService
) {
    suspend fun createGoal(uid: String, goal: Goal) = goalService.createGoal(uid, goal)
    suspend fun getGoals(uid: String) = goalService.getGoals(uid)
    suspend fun getGoalById(uid: String, goalId: String): Goal? =
        goalService.getGoalById(uid, goalId)


    suspend fun updateGoal(uid: String, goal: Goal) = goalService.updateGoal(uid, goal)
    suspend fun deleteGoal(uid: String, goalId: String) = goalService.deleteGoal(uid, goalId)
}
