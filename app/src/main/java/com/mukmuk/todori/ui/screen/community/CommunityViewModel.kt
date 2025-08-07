package com.mukmuk.todori.ui.screen.community

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mukmuk.todori.data.remote.community.StudyPost
import com.mukmuk.todori.data.repository.CommunityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val repository: CommunityRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CommunityState())
    val state: StateFlow<CommunityState> = _state.asStateFlow()

    var selectedPost by mutableStateOf<StudyPost?>(null)

    var commentList = null

    var menu = listOf("수정", "삭제")

    var td = listOf("답글 달기", "삭제")

    var data = 1

    var isLoading by mutableStateOf(false)


    fun loadPosts(fliter: String? = null) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val posts = repository.getPosts(fliter)
                _state.update {
                    it.copy(
                        postList = posts,
                        isLoading = false,
                        error = null
                    )
                }
            }
            catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }


    fun createPost(post: StudyPost) {
        viewModelScope.launch {
            try {
                repository.createPost(post)
                _state.update {
                    it.copy(postList = it.postList + post, error = null)
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun updatePost(post: StudyPost) {

    }

    fun deletePost(post: StudyPost) {
        viewModelScope.launch {
            try {
                repository.deletePost(post.postId)
                _state.update {
                    it.copy(postList = it.postList - post, error = null)
                }
            }
            catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }



}