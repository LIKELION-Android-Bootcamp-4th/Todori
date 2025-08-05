package com.mukmuk.todori.data.repository

import com.mukmuk.todori.data.remote.study.Study
import com.mukmuk.todori.data.remote.study.StudyMember
import com.mukmuk.todori.data.service.StudyService
import javax.inject.Inject

class StudyRepository @Inject constructor(
    private val studyService: StudyService
) {
    suspend fun createStudy(study: Study, leaderMember: StudyMember) =
        studyService.createStudy(study, leaderMember)
}
