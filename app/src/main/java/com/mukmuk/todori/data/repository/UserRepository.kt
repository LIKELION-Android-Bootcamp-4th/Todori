package com.mukmuk.todori.data.repository

import android.util.Log
import com.mukmuk.todori.data.remote.user.User
import com.mukmuk.todori.data.service.UserService
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userService: UserService
) {
    //프로필 조회
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
}
