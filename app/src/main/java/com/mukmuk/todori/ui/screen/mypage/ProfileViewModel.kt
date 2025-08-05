package com.mukmuk.todori.ui.screen.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mukmuk.todori.data.remote.user.User
import com.mukmuk.todori.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: UserRepository
): ViewModel() {

    private val _profile = MutableStateFlow<User?>(null)
    val profile: StateFlow<User?> = _profile

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
}
