package com.mukmuk.todori.data.repository

import com.mukmuk.todori.data.remote.user.StudyTargets
import com.mukmuk.todori.data.service.StudyTargetsService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
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

    fun targetsFlow(uid: String): Flow<StudyTargets?> = studyTargetsService.targetsFlow(uid)

}
