package com.mukmuk.todori.data.repository

import android.util.Log
import com.mukmuk.todori.data.remote.user.User
import com.mukmuk.todori.data.service.UserService
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userService: UserService
){
    suspend fun createUser(uid: String, user: User) {
        try {
            userService.createUser(uid, user)
        }catch (e: Exception) {
            Log.d("User", "Repository 에러 : ${e.message}")
        }
    }
    suspend fun getProfile(uid: String): User? {
        return try {
            userService.getProfile(uid)
        } catch (e: Exception) {
            Log.e("Profile", "프로필 조회 실패: ${e.message}")
            null
        }
    }

    suspend fun getUser(uid: String) {

    //프로필 수정
    suspend fun updateUser(uid: String, nickname: String, intro: String){
            userService.updateUser(uid, nickname, intro)
    }

    suspend fun updateUser(uid: String, user: User) {
        userService.updateUser(uid, user)
    }

    suspend fun deleteUser(uid: String) {
        userService.deleteUser(uid)
    }
}