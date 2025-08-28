package com.mukmuk.todori.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import com.mukmuk.todori.data.remote.study.MyStudy
import com.mukmuk.todori.data.remote.study.Study
import com.mukmuk.todori.data.remote.study.StudyMember
import com.mukmuk.todori.data.remote.study.StudyTodo
import com.mukmuk.todori.data.remote.study.TodoProgress
import com.mukmuk.todori.data.service.StudyService
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import javax.inject.Inject

class StudyRepository @Inject constructor(
    private val studyService: StudyService
) {
    suspend fun createStudy(study: Study, leaderMember: StudyMember,uid: String) {
        val myStudy = MyStudy(
            studyId = study.studyId,
            description = study.description,
            activeDays = study.activeDays,
            studyName = study.studyName,
            joinedAt = study.createdAt,
            role = leaderMember.role,
            nickname = leaderMember.nickname,
        )
        studyService.createStudy(study, leaderMember, myStudy,uid)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun addMyStudyForMember(uid: String, study: Study, nickname: String) {
        val localDate = LocalDate.now()
        val date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
        val firebaseTimestamp = Timestamp(date)
        val myStudy = MyStudy(
            studyId = study.studyId,
            studyName = study.title,
            description = study.description,
            activeDays = study.activeDays,
            joinedAt = firebaseTimestamp,
            role = "MEMBER",
            nickname = nickname,
            status = "ACTIVE",
            hasPosted = false
        )
        studyService.addMyStudyForMember(uid, myStudy)
    }

    suspend fun getMyStudies(uid: String): List<MyStudy> =
        studyService.getMyStudies(uid)

    suspend fun getStudies(studyIds: List<String>): List<Study> =
        studyService.getStudies(studyIds)

    suspend fun getTodosForStudies(studyIds: List<String>, date: String?): List<StudyTodo> =
        studyService.getTodosForStudies(studyIds, date)

    suspend fun getMyAllProgresses(uid: String): List<TodoProgress> =
        studyService.getMyAllProgresses(uid)

    suspend fun getMembersForStudies(studyIds: List<String>): List<StudyMember> =
        studyService.getMembersForStudies(studyIds)

    suspend fun getProgressByUidStudyDate(uid: String, studyId: String, date: String): List<TodoProgress> =
        studyService.getProgressByUidStudyDate(uid, studyId, date)

    suspend fun getProgressesByStudyAndDate(studyId: String, date: String): List<TodoProgress> =
        studyService.getProgressesByStudyAndDate(studyId, date)

    suspend fun toggleTodoProgressDone(studyId: String, studyTodoId: String, uid: String, checked: Boolean) =
        studyService.toggleTodoProgressDone(studyId, studyTodoId, uid, checked)

    suspend fun deleteStudyTodo(studyTodoId: String) =
        studyService.deleteStudyTodo(studyTodoId)

    suspend fun addStudyTodoWithProgress(
        studyTodo: StudyTodo,
        members: List<StudyMember>
    ) = studyService.addStudyTodoWithProgress(studyTodo, members)

    suspend fun deleteStudyWithAllData(studyId: String) =
        studyService.deleteStudyWithAllData(studyId)

    suspend fun removeMemberFromStudy(studyId: String, uid: String) =
        studyService.removeMemberFromStudy(studyId, uid)

    suspend fun updateStudy(study: Study) =
        studyService.updateStudy(study)

    suspend fun updateMyStudyNickname(uid: String, studyId: String, nickname: String) =
        studyService.updateMyStudyNickname(uid, studyId, nickname)

    suspend fun leaveStudy(studyId: String, uid: String) = studyService.leaveStudy(studyId, uid)

    suspend fun updateHasPosted(uid: String, studyId: String, hasPosted: Boolean) =
        studyService.updateHasPosted(uid, studyId, hasPosted)

}
