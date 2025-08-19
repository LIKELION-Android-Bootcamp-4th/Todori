package com.mukmuk.todori.ui.screen.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.mukmuk.todori.data.remote.user.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LevelViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private var removeListener: (() -> Unit)? = null

    fun observe(uid: String) {
        removeListener?.invoke()
        val reg = firestore.collection("users")
            .document(uid)
            .addSnapshotListener { snap, _ ->
                viewModelScope.launch {
                    _user.value = snap?.toObject(User::class.java)
                }
            }
        removeListener = { reg.remove() }
    }

    override fun onCleared() {
        super.onCleared()
        removeListener?.invoke()
        removeListener = null
    }
}
