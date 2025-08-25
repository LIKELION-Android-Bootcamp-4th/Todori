package com.mukmuk.todori.data.remote.study

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize


@Parcelize
data class MyStudy(
    val studyId: String = "",                   // 스터디 ID
    val studyName: String = "",                 // 스터디 이름 (리스트 표시용)
    val description: String = "",               // study 설명
    val activeDays: List<String> = emptyList(), //스터디 활동일
    val joinedAt: Timestamp? = null,            // 참여 시간
    val role: String = "MEMBER",                // "LEADER" or "MEMBER"
    val nickname: String = "",                  // 내 닉네임
    val status: String = "ACTIVE"               // "ACTIVE", "LEFT", 등 상태
): Parcelable
