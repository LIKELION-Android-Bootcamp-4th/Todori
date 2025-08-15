package com.mukmuk.todori.ui.screen.community.detail

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.mukmuk.todori.data.remote.community.StudyPost
import com.mukmuk.todori.data.remote.community.StudyPostComment
import com.mukmuk.todori.data.remote.study.StudyMember
import com.mukmuk.todori.data.repository.CommunityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import javax.inject.Inject


@HiltViewModel
class CommunityDetailViewModel@Inject constructor(
    private val repository: CommunityRepository,
): ViewModel() {

    private val _state = MutableStateFlow(CommunityDetailState())
    val state: StateFlow<CommunityDetailState> = _state


    val menu = listOf("수정", "삭제")

    val td = listOf("답글 달기", "삭제")

    private var loadPostJob: Job? = null

    private var commentJob: Job? = null

    fun loadPostById(postId: String) {
        loadPostJob?.cancel()
        loadPostJob = viewModelScope.launch {
            _state.update { it.copy(isLoading = true, study = null, user = null, post = null, commentList = emptyList(), commentReplyList = emptyMap(), replyToCommentId = null) }
            try {
                repository.getPostById(postId).collect { post ->
                    val user = repository.getUserById(post!!.createdBy)
                    var members = emptyList<StudyMember>()
                    if(post.studyId.isNotBlank()) {
                        loadStudyById(post.studyId)
                        members = repository.getStudyMembers(post.studyId)
                    }

                    _state.update {
                        it.copy(
                            post = post.copy(
                                userName = user?.nickname ?: "",
                                level = user?.level ?: 0,
                                memberCount = members.size
                            ),
                            isLoading = false,
                            error = null
                        )
                    }

                    getComments(postId)

                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun getUserById(uid: String) {
        viewModelScope.launch {
            try {
                val user = repository.getUserById(uid)
                _state.update {
                    it.copy(
                        user = user,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
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
            _state.update { it.copy(study = null) }
            try {
                repository.updatePost(postId, updatedPost)
                _state.update {
                    it.copy(
                        post = updatedPost,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
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
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }


    fun createComment(postId: String, reply: StudyPostComment) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                repository.createPostComment(postId, reply)
                getComments(postId)
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }

    }

    fun getComments(postId: String) {
        commentJob?.cancel()
        commentJob = viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val comments = repository.getPostComments(postId)
                val updatedComments = comments.sortedBy {
                    it.createdAt?.toDate()?.time
                }

                val repliesMap = mutableMapOf<String, List<StudyPostComment>>()
                for (comment in comments) {
                    val replies = repository.getPostCommentReplies(postId, comment.commentId)
                    val updatedReplies = replies.sortedBy {
                        it.createdAt?.toDate()?.time
                    }
                    repliesMap[comment.commentId] = updatedReplies
                }
                _state.update {
                    it.copy(
                        commentList = updatedComments,
                        commentReplyList = repliesMap,
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
                val replies = repository.getPostCommentReplies(postId, replyId)
                for (reply in replies) {
                    repository.deletePostComment(postId, reply.commentId)
                }
                repository.deletePostComment(postId, replyId)
                getComments(postId)
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun deleteAllComments(postId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val comments = repository.getPostComments(postId)
                for (comment in comments) {
                    val replies = repository.getPostCommentReplies(postId, comment.commentId)
                    for (reply in replies) {
                        repository.deletePostComment(postId, reply.commentId)
                        }
                    repository.deletePostComment(postId, comment.commentId)
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
                repository.createPostCommentReply(postId, commentId, reply)
                _state.update { it.copy(replyToCommentId = null) }
                getComments(postId)
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun setReplyToCommentId(commentId: String? = null) {
        _state.update { it.copy(replyToCommentId = commentId) }
    }

    @SuppressLint("SimpleDateFormat")
    fun formatDate(timestamp: Timestamp? = null): String {
        val date = timestamp?.toDate()
        val format = SimpleDateFormat("yyyy-MM-dd")
        return format.format(date)
    }

    fun loadStudyById(studyId: String) {
        viewModelScope.launch {
            try {
                repository.loadStudyById(studyId).collect { study ->
                    _state.update {
                        it.copy(
                            study = study,
                            error = null
                        )
                    }
                    if (study != null) {
                        loadStudyMember(studyId)
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun loadStudyMember(studyId: String) {
        viewModelScope.launch {
            try {
                val members = repository.getStudyMembers(studyId)
                _state.update {
                    it.copy(
                        memberList = members,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun updateStudyMember(postId: String, studyId: String, member: StudyMember) {
        viewModelScope.launch {
            try {
                repository.updateStudyMember(postId, studyId, member)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "저장 실패, 다시 시도해주세요.")
            }
        }
    }

}