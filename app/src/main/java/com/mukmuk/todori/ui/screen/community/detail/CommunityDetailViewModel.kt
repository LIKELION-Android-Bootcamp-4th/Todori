package com.mukmuk.todori.ui.screen.community.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mukmuk.todori.data.remote.community.StudyPost
import com.mukmuk.todori.data.remote.community.StudyPostComment
import com.mukmuk.todori.data.remote.study.StudyMember
import com.mukmuk.todori.data.repository.CommunityRepository
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


    val menu = listOf("수정", "삭제")

    val td = listOf("답글 달기", "삭제")

    fun loadPostById(postId: String) {
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
                if (updatedPost.studyId.isNotBlank()) {
                    loadStudyById(updatedPost.studyId)
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
                deleteAllComments(postId)
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


    fun createComment(postId: String, reply: StudyPostComment) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                repository.createReply(postId, reply)
                getComments(postId)
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }

    }

    fun getComments(postId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val comments = repository.getReplies(postId)
                val commentsMap = mutableMapOf<String, List<StudyPostComment>>()
                for(comment in comments) {
                    commentsMap[comment.commentId] = repository.getCommentReplies(postId, comment.commentId)
                }
                _state.update {
                    it.copy(
                        commentList = comments,
                        commentReplyList = commentsMap.toMap(),
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun deleteComment(postId: String, replyId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val replies = repository.getCommentReplies(postId, replyId)
                for (reply in replies) {
                    repository.deleteReply(postId, reply.commentId)
                }
                repository.deleteReply(postId, replyId)
                getComments(postId)
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun deleteAllComments(postId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val comments = repository.getReplies(postId)
                for (comment in comments) {
                    repository.deleteReply(postId, comment.commentId)
                }
                getComments(postId)
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun createCommentReply(postId: String, commentId: String, reply: StudyPostComment) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                repository.createCommentReply(postId, commentId, reply)
                _state.update { it.copy(replyToCommentId = null) }
                getComments(postId)
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun getCommentReplies(postId: String, commentId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val replies = repository.getCommentReplies(postId, commentId)
                _state.update { state ->
                    val updatedCommentReplyList = state.commentReplyList.toMutableMap()
                    updatedCommentReplyList[commentId] = replies
                    state.copy(
                        commentReplyList = updatedCommentReplyList.toMap(),
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun setReplyToCommentId(commentId: String? = null) {
        _state.update { it.copy(replyToCommentId = commentId) }
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