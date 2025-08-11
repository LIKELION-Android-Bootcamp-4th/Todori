package com.mukmuk.todori.ui.screen.community.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mukmuk.todori.data.remote.community.StudyPost
import com.mukmuk.todori.data.remote.community.StudyPostComment
import com.mukmuk.todori.data.remote.study.StudyMember
import com.mukmuk.todori.data.repository.CommunityRepository
import com.mukmuk.todori.ui.screen.community.CommunityViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CommunityDetailViewModel@Inject constructor(
    private val repository: CommunityRepository,
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
            _state.update { it.copy(isLoading = true, study = null) }
            try {
                val post = repository.getPostById(postId)
                _state.update {
                    it.copy(
                        post = post,
                        isLoading = false,
                        error = null
                    )
                }
                if (post != null) {
                    if(post.studyId.isNotBlank()) {
                        loadStudyById(post.studyId)
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun createPost(post: StudyPost) {
        viewModelScope.launch {
            try {
                repository.createPost(post)
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

    fun updatePost(postId: String, updatedPost: StudyPost) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, study = null) }
            try {
                repository.updatePost(postId, updatedPost)
                _state.update {
                    it.copy(
                        post = updatedPost,
                        isLoading = false,
                        error = null
                    )
                }
                if (updatedPost != null) {
                    if (updatedPost.studyId.isNotBlank()) {
                        loadStudyById(updatedPost.studyId)
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            try {
                repository.deletePost(postId)
                _state.update {
                    it.copy(
                        post = null,
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
                getReplies(postId)
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }

    }

    fun getReplies(postId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val comments = repository.getReplies(postId)
                val commentList: MutableMap<String, List<StudyPostComment>> = mutableMapOf()
                comments.forEach { comment ->
                    val replies = repository.getCommentReplies(postId, comment.commentId)
                    commentList[comment.commentId] = replies
                }
                _state.update {
                    it.copy(
                        commentList = commentList,
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
                        commentList = mapOf(commentId to replies),
                        isLoading = false,
                        error = null,
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun loadStudyById(studyId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val study = repository.loadStudyById(studyId)
                _state.update {
                    it.copy(
                        study = study,
                        isLoading = false,
                        error = null
                    )
                }
                if (study != null) {
                    loadStudyMember(studyId)
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun loadStudyMember(studyId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val members = repository.getStudyMembers(studyId)
                _state.update {
                    it.copy(
                        memberList = members,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun updateStudyMember(studyId: String, member: StudyMember) {
        viewModelScope.launch {
            try {
                repository.updateStudyMember(studyId, member)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "저장 실패, 다시 시도해주세요.")
            }
        }
    }

}