package com.mukmuk.todori.ui.screen.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.mukmuk.todori.data.local.datastore.AuthLocalRepository
import com.mukmuk.todori.data.local.datastore.HomeSettingRepository
import com.mukmuk.todori.data.local.datastore.RecordSettingRepository
import com.mukmuk.todori.data.local.datastore.TodayTodoRepository
import com.mukmuk.todori.data.remote.user.User
import com.mukmuk.todori.data.repository.UserRepository
import com.mukmuk.todori.data.service.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authLocalRepository: AuthLocalRepository,
    private val repository: UserRepository,
    private val homeRepository: HomeSettingRepository,
    private val recordRepository: RecordSettingRepository,
    private val todayTodoRepository: TodayTodoRepository,
    private val authService: AuthService
): ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState(isLoading = true))
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<ProfileEffect>(extraBufferCapacity = 1)
    val effect: SharedFlow<ProfileEffect> = _effect

    //프로필 조회
    fun loadProfile(uid: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            runCatching { repository.getProfile(uid) }
                .onSuccess { user ->
                    _uiState.value = _uiState.value.copy(isLoading = false, user = user, error = null)
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                    _effect.tryEmit(ProfileEffect.ShowMessage("프로필을 불러오지 못했어요"))
                }
        }
    }


    fun updateProfile(uid: String, nickname: String, intro: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdating = true, error = null)
            runCatching {
                repository.updateUser(uid, nickname, intro)
                repository.getProfile(uid)
            }.onSuccess { user ->
                _uiState.value = _uiState.value.copy(isUpdating = false, user = user, error = null)
                _effect.tryEmit(ProfileEffect.ShowMessage("프로필이 저장됐어요"))
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(isUpdating = false, error = e.message)
                _effect.tryEmit(ProfileEffect.ShowMessage("프로필 저장 실패"))
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            val provider = _uiState.value.user?.authProvider // "google.com" | "kakao" | "naver"
            _uiState.value = _uiState.value.copy(isLoggingOut = true, error = null)

            if (provider != null) {
                authLocalRepository.saveLastLoginProvider(provider)
            }

            runCatching { authService.logout(provider) }
                .onSuccess {
                    homeRepository.clearHomeSetting()
                    recordRepository.clearRecordSetting()
                    todayTodoRepository.clearTodoSetting()
                    _uiState.value = _uiState.value.copy(isLoggingOut = false)
                    _effect.tryEmit(ProfileEffect.LoggedOut)
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(isLoggingOut = false, error = e.message)
                    _effect.tryEmit(ProfileEffect.ShowMessage("로그아웃에 실패했어요"))
                }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            val user: User = _uiState.value.user ?: return@launch
            _uiState.value = _uiState.value.copy(isDeleting = true, error = null)

            try {
                homeRepository.clearHomeSetting()
                recordRepository.clearRecordSetting()
                todayTodoRepository.clearTodoSetting()
                authService.deleteAccount(user.uid, user.authProvider)
                _uiState.value = _uiState.value.copy(isDeleting = false)
                _effect.tryEmit(ProfileEffect.DeleteSuccess)
            } catch (e: FirebaseAuthRecentLoginRequiredException) {
                _uiState.value = _uiState.value.copy(isDeleting = false)
                _effect.tryEmit(ProfileEffect.NeedsReauth)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isDeleting = false, error = e.message)
                _effect.tryEmit(ProfileEffect.ShowMessage("회원탈퇴에 실패했어요"))
            }
        }
    }
}
