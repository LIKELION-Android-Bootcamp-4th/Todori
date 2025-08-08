package com.mukmuk.todori.data.repository

import android.content.Context
import android.util.Log
import com.mukmuk.todori.data.remote.user.User
import com.mukmuk.todori.data.service.UserService
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userService: UserService
){
    suspend fun getProfile(uid: String): User? {
        return try {
            userService.getProfile(uid)
        } catch (e: Exception) {
            Log.e("Profile", "프로필 조회 실패: ${e.message}")
            null
        }
    }

    //프로필 수정
    suspend fun updateUser(uid: String, nickname: String, intro: String){
            userService.updateUser(uid, nickname, intro)
    }

    //마지막 로그인 시간 업데이트
    suspend fun updateLastLogin(uid: String) {
        try {
            userService.updateLastLogin(uid)
        } catch (e: Exception) {
            Log.e("UserRepository", "마지막 로그인 시간 업데이트 실패: ${e.message}")
        }
    }

    suspend fun loginWithKakao(context: Context) {
        try {
            userService.kakaoLogin(context)
        } catch (e: Exception) {
            Log.e("UserRepository", "카카오 로그인 실패: ${e.message}")
            throw e
        }
    }



}