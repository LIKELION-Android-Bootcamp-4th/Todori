package com.mukmuk.todori.data.remote.user

import com.google.firebase.Timestamp

data class User(
    val uid: String = "",                // 고유 식별자
    val nickname: String = "",           // 사용자가 설정한 닉네임
    val intro: String? = null,           // 한 줄 소개 or 상태 메세지
    val level: Int = 1,                  // 사용자 레벨
    val rewardPoint: Int = 0,            // 포인트
    val authProvider: String = "",       // google, kakao, naver
    val createdAt: Timestamp? = null,            // 계정 생성 시각
    val lastLoginAt: Timestamp? = null,          // 마지막 로그인 시각
    val isDeleted: Boolean = false,      // 탈퇴 여부
    val fcmToken: String? = null         // 알림 푸시용 FCM 토큰
)