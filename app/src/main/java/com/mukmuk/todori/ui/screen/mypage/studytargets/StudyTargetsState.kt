package com.mukmuk.todori.ui.screen.mypage.studytargets

import com.mukmuk.todori.data.remote.user.StudyTargets

data class StudyTargetsState(
    val targets: StudyTargets = StudyTargets(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val showConsistencyCheck: Boolean = false
) {
    companion object {
        const val MAX_DAILY_MINUTES = 12 * 60
        const val MAX_WEEKLY_MINUTES = 50 * 60
        const val MAX_MONTHLY_MINUTES = 200 * 60

        const val DAILY_STEP = 30
        const val WEEKLY_STEP = 60
        const val MONTHLY_STEP = 60
    }
}