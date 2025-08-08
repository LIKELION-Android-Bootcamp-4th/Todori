package com.mukmuk.todori.ui.screen.community.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mukmuk.todori.data.remote.community.StudyPostComment
import com.mukmuk.todori.data.repository.CommunityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@HiltViewModel
class CommunityDetailViewModel@Inject constructor(
    private val repository: CommunityRepository
): ViewModel() {

    private val _state = MutableStateFlow(CommunityDetailState())
    val state: StateFlow<CommunityDetailState> = _state


    var menu = listOf("수정", "삭제")

    var td = listOf("답글 달기", "삭제")

    fun loadPostById(postId: String) {
        if ((_state.value.post?.postId ?: "") == postId) {
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val post = repository.getPostById(postId)
                _state.update {
                    it.copy(
                        post = post,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun createReply(postId: String, reply: StudyPostComment) {
        viewModelScope.launch {
            try {
                repository.createReply(postId, reply)
                loadPostById(postId)
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }

    }

    fun getReplies(postId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val replies = repository.getReplies(postId)
                replies.forEach {
                    it.parentCommentId?.let { it1 -> getCommentReplies(postId, it1) }
                }
                _state.update {
                    it.copy(
                        commentList = replies,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun deleteReply(postId: String, replyId: String) {
        viewModelScope.launch {
            try {
                repository.deleteReply(postId, replyId)
                loadPostById(postId)
                getReplies(postId)
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun createCommentReply(postId: String, commentId: String, reply: StudyPostComment) {
        viewModelScope.launch {
            try {
                repository.createCommentReply(postId, commentId, reply)
                loadPostById(postId)
                getReplies(postId)
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun getCommentReplies(postId: String, commentId: String) {
        viewModelScope.launch {
            try {
                val replies = repository.getCommentReplies(postId, commentId)
                _state.update {
                    it.copy(
                        commentList = replies,
                        isLoading = false,
                        error = null
                    )
                }
                } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

}