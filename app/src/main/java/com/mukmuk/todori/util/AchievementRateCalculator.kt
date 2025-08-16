package com.mukmuk.todori.util

// 달성 확률 계산 함수들 - 임의 지정 값
object AchievementRateCalculator {

    /**
     * 일간 목표 달성 확률 계산
     * - 1-2시간: 높은 달성률 (80-95%)
     * - 3-4시간: 보통 달성률 (60-80%)
     * - 5시간 이상: 낮은 달성률 (30-60%)
     */
    fun calculateDailyAchievementRate(minutes: Int): Float {
        val hours = minutes / 60f
        return when {
            hours <= 0 -> 0f
            hours <= 1f -> 0.95f
            hours <= 2f -> 0.85f + (2f - hours) * 0.10f // 85-95%
            hours <= 3f -> 0.70f + (3f - hours) * 0.15f // 70-85%
            hours <= 4f -> 0.55f + (4f - hours) * 0.15f // 55-70%
            hours <= 6f -> 0.35f + (6f - hours) * 0.10f // 35-55%
            hours <= 8f -> 0.20f + (8f - hours) * 0.075f // 20-35%
            else -> 0.15f // 15%
        }.coerceIn(0.1f, 0.95f)
    }

    /**
     * 주간 목표 달성 확률 계산
     * - 7-14시간: 높은 달성률 (80-90%)
     * - 15-25시간: 보통 달성률 (60-80%)
     * - 26시간 이상: 낮은 달성률 (30-60%)
     */
    fun calculateWeeklyAchievementRate(minutes: Int): Float {
        val hours = minutes / 60f
        return when {
            hours <= 0 -> 0f
            hours <= 7f -> 0.90f
            hours <= 14f -> 0.80f + (14f - hours) * 0.10f / 7f // 80-90%
            hours <= 21f -> 0.65f + (21f - hours) * 0.15f / 7f // 65-80%
            hours <= 28f -> 0.45f + (28f - hours) * 0.20f / 7f // 45-65%
            hours <= 35f -> 0.30f + (35f - hours) * 0.15f / 7f // 30-45%
            else -> 0.25f // 25%
        }.coerceIn(0.15f, 0.90f)
    }

    /**
     * 월간 목표 달성 확률 계산
     * - 30-60시간: 높은 달성률 (80-90%)
     * - 61-100시간: 보통 달성률 (60-80%)
     * - 101시간 이상: 낮은 달성률 (30-60%)
     */
    fun calculateMonthlyAchievementRate(minutes: Int): Float {
        val hours = minutes / 60f
        return when {
            hours <= 0 -> 0f
            hours <= 20f -> 0.85f
            hours <= 40f -> 0.80f + (40f - hours) * 0.05f / 20f // 80-85%
            hours <= 60f -> 0.70f + (60f - hours) * 0.10f / 20f // 70-80%
            hours <= 80f -> 0.55f + (80f - hours) * 0.15f / 20f // 55-70%
            hours <= 100f -> 0.40f + (100f - hours) * 0.15f / 20f // 40-55%
            hours <= 120f -> 0.30f + (120f - hours) * 0.10f / 20f // 30-40%
            else -> 0.25f // 25%
        }.coerceIn(0.15f, 0.85f)
    }

}