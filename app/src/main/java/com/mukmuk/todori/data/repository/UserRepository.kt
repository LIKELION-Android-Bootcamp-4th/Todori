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

    suspend fun getUser(uid: String) {

    }

    suspend fun updateUser(uid: String, user: User) {
        userService.updateUser(uid, user)
    }

    suspend fun deleteUser(uid: String) {
        userService.deleteUser(uid)
    }
}