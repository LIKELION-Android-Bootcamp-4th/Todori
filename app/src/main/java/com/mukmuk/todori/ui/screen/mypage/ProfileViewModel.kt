package com.mukmuk.todori.ui.screen.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mukmuk.todori.data.remote.user.User
import com.mukmuk.todori.data.repository.UserRepository
import com.mukmuk.todori.data.service.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: UserRepository,
    private val authService: AuthService
): ViewModel() {

    private val _profile = MutableStateFlow<User?>(null)
    val profile: StateFlow<User?> = _profile

    private val _logoutDone = MutableStateFlow<Boolean?>(null)
    val logoutDone: StateFlow<Boolean?> = _logoutDone

    //프로필 조회
    fun loadProfile(uid: String) {
        viewModelScope.launch {
            _profile.value = repository.getProfile(uid)
        }
    }

    //프로필 수정
    fun updateProfile(uid: String, nickname: String, intro: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.updateUser(uid, nickname, intro)
            loadProfile(uid)
            onSuccess()
        }
    }

    fun logout() {
        viewModelScope.launch {
            val provider = _profile.value?.authProvider // "google.com" | "kakao" | "naver"
            runCatching { authService.logout(provider) }
                .onSuccess { _logoutDone.value = true }
                .onFailure { _logoutDone.value = false }
        }
    }
}
