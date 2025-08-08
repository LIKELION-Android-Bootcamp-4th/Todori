package com.mukmuk.todori.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.mukmuk.todori.data.service.GoalService
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class GoalStatsRepository @Inject constructor(
    private val goalService: GoalService
) {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getCompletedGoalCount(uid: String, year: Int, month: Int): Int {
        val goals = goalService.getGoals(uid)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        return goals.count { goal ->
            goal.completed && runCatching {
                val end = LocalDate.parse(goal.endDate, formatter)
                end.year == year && end.monthValue == month
            }.getOrDefault(false)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getTotalGoalCount(uid: String, year: Int, month: Int): Int {
        val goals = goalService.getGoals(uid)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        return goals.count { goal ->
            runCatching {
                val end = LocalDate.parse(goal.endDate, formatter)
                end.year == year && end.monthValue == month
            }.getOrDefault(false)
        }
    }
}
