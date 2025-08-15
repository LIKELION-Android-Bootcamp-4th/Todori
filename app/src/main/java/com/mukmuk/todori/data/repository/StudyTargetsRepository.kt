package com.mukmuk.todori.data.repository

import com.mukmuk.todori.data.remote.user.StudyTargets
import com.mukmuk.todori.data.service.StudyTargetsService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class StudyTargetsRepository @Inject constructor(
    private val studyTargetsService: StudyTargetsService
) {

    suspend fun getStudyTargets(uid: String): StudyTargets? =
        withContext(Dispatchers.IO) {
            studyTargetsService.getStudyTargets(uid)
        }

    suspend fun updateStudyTargets(uid: String, targets: StudyTargets) =
        withContext(Dispatchers.IO) {
            studyTargetsService.updateStudyTargets(uid, targets)
        }

    suspend fun updateDailyTarget(uid: String, minutes: Int) =
        withContext(Dispatchers.IO) {
            studyTargetsService.updateDailyTarget(uid, minutes)
        }

    suspend fun updateWeeklyTarget(uid: String, minutes: Int) =
        withContext(Dispatchers.IO) {
            studyTargetsService.updateWeeklyTarget(uid, minutes)
        }

    suspend fun updateMonthlyTarget(uid: String, minutes: Int) =
        withContext(Dispatchers.IO) {
            studyTargetsService.updateMonthlyTarget(uid, minutes)
        }

    fun validateTargetConsistency(targets: StudyTargets): TargetConsistency {
        val daily = targets.dailyMinutes ?: 0
        val weekly = targets.weeklyMinutes ?: 0
        val monthly = targets.monthlyMinutes ?: 0

        val expectedWeeklyFromDaily = daily * 7
        val expectedMonthlyFromDaily = daily * 30
        val expectedMonthlyFromWeekly = weekly * 4

        val weeklyConsistent = if (weekly > 0 && daily > 0) {
            kotlin.math.abs(weekly - expectedWeeklyFromDaily) <= (expectedWeeklyFromDaily * 0.15) // 15% 오차 허용
        } else true

        val monthlyFromDailyConsistent = if (monthly > 0 && daily > 0) {
            kotlin.math.abs(monthly - expectedMonthlyFromDaily) <= (expectedMonthlyFromDaily * 0.15)
        } else true

        val monthlyFromWeeklyConsistent = if (monthly > 0 && weekly > 0) {
            kotlin.math.abs(monthly - expectedMonthlyFromWeekly) <= (expectedMonthlyFromWeekly * 0.15)
        } else true

        return TargetConsistency(
            isConsistent = weeklyConsistent && monthlyFromDailyConsistent && monthlyFromWeeklyConsistent,
            weeklyVsDaily = if (!weeklyConsistent) "주간 목표가 일간 목표와 맞지 않아요" else null,
            monthlyVsDaily = if (!monthlyFromDailyConsistent) "월간 목표가 일간 목표와 맞지 않아요" else null,
            monthlyVsWeekly = if (!monthlyFromWeeklyConsistent) "월간 목표가 주간 목표와 맞지 않아요" else null,
            suggestions = generateSuggestions(daily, weekly, monthly)
        )
    }

    private fun generateSuggestions(daily: Int, weekly: Int, monthly: Int): List<String> {
        val suggestions = mutableListOf<String>()

        if (daily > 0) {
            suggestions.add("일간 ${daily}분 기준 → 주간 ${daily * 7}분, 월간 ${daily * 30}분 권장")
        }
        if (weekly > 0 && daily == 0) {
            val suggestedDaily = weekly / 7
            suggestions.add("주간 ${weekly}분 기준 → 일간 ${suggestedDaily}분 권장")
        }
        if (monthly > 0 && daily == 0 && weekly == 0) {
            val suggestedDaily = monthly / 30
            val suggestedWeekly = monthly / 4
            suggestions.add("월간 ${monthly}분 기준 → 일간 ${suggestedDaily}분, 주간 ${suggestedWeekly}분 권장")
        }

        return suggestions
    }
}

data class TargetConsistency(
    val isConsistent: Boolean,
    val weeklyVsDaily: String?,
    val monthlyVsDaily: String?,
    val monthlyVsWeekly: String?,
    val suggestions: List<String>
)