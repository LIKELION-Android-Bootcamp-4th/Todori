package com.mukmuk.todori.data.repository

import android.app.Activity
import android.content.Context
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
            null
        }
    }

    suspend fun getUsers(uids: Set<String>): List<User> {
        return try {
            userService.getUsers(uids)
        } catch (e: Exception) {
            emptyList()
        }
    }


    suspend fun updateUser(uid: String, nickname: String, intro: String){
            userService.updateUser(uid, nickname, intro)
    }

    suspend fun updateLastLogin(uid: String) {
        try {
            userService.updateLastLogin(uid)
        } catch (e: Exception) {
        }
    }

    suspend fun loginWithKakao(context: Context) {
        try {
            userService.kakaoLogin(context)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun loginWithNaver(activity: Activity) {
        userService.naverLogin(activity)
    }

}