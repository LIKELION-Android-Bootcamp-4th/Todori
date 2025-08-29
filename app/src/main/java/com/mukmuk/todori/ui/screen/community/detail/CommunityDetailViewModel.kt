package com.mukmuk.todori.ui.screen.community.detail

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.mukmuk.todori.data.remote.community.StudyPostComment
import com.mukmuk.todori.data.remote.study.Study
import com.mukmuk.todori.data.remote.study.StudyMember
import com.mukmuk.todori.data.repository.CommunityRepository
import com.mukmuk.todori.data.repository.StudyRepository
import com.mukmuk.todori.ui.screen.community.CommentUiModel
import com.mukmuk.todori.ui.screen.community.PostUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import javax.inject.Inject


@HiltViewModel
class CommunityDetailViewModel @Inject constructor(
    private val repository: CommunityRepository,
    private val studyRepository: StudyRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _state = MutableStateFlow(CommunityDetailState())
    val state: StateFlow<CommunityDetailState> = _state


    val menu = listOf("수정", "삭제")
    private var loadPostJob: Job? = null
    private var commentJob: Job? = null

    fun loadPostById(postId: String) {
        loadPostJob?.cancel()
        loadPostJob = viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    study = null,
                    user = null,
                    post = null,
                    commentList = emptyList(),
                    replyToCommentId = null
                )
            }
            try {
                repository.getPostById(postId).collect { post ->
                    val user = repository.getUserById(post!!.createdBy)
                    var members = emptyList<StudyMember>()
                    if (post.studyId.isNotBlank()) {
                        loadStudyById(post.studyId)
                        members = repository.getStudyMembers(post.studyId)
                    }

                    val postUiModel = PostUiModel(
                        postId = post.postId,
                        studyId = post.studyId,
                        title = post.title,
                        content = post.content,
                        userId = post.createdBy,
                        nickname = user?.nickname ?: "알 수 없음",
                        level = user?.level ?: 0,
                        tags = post.tags,
                        memberCount = members.size,
                        commentsCount = post.commentsCount,
                        createdAt = post.createdAt,
                        updatedAt = post.updatedAt,
                    )

                    _state.update {
                        it.copy(
                            post = postUiModel,
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

    fun deletePost(postId: String) {
        viewModelScope.launch {
            val post = state.value.post ?: return@launch

            repository.deletePost(postId)
            deleteAllComments(postId)

            post.studyId.takeIf { it.isNotBlank() }?.let { studyId ->
                val uid = auth.currentUser?.uid ?: return@launch
                studyRepository.updateHasPosted(uid, studyId, false)
            }

            _state.update {
                it.copy(
                    post = null,
                    error = null
                )
            }
        }
    }


    fun createComment(postId: String, reply: StudyPostComment) {
        viewModelScope.launch {
            try {
                val created = repository.createPostComment(postId, reply)
                val user = repository.getUserById(reply.uid)
                val uiModel = CommentUiModel(
                    commentId = created.commentId,
                    postId = created.postId,
                    uid = created.uid,
                    nickname = user?.nickname ?: "알 수 없음",
                    level = user?.level ?: 0,
                    content = created.content,
                    createdAt = created.createdAt
                )
                addCommentToState(uiModel)
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }

    }

    fun getComments(postId: String) {
        commentJob?.cancel()
        commentJob = viewModelScope.launch {
            try {
                var comments = emptyList<StudyPostComment>()
                var updatedComments = emptyList<CommentUiModel>()

                coroutineScope {
                    async {
                        comments = repository.getPostComments(postId)
                        // uid → user 정보 조회 후 CommentUiModel 변환
                        updatedComments = comments.sortedBy { it.createdAt?.toDate()?.time }
                            .map { comment ->
                                val user = repository.getUserById(comment.uid)
                                CommentUiModel(
                                    commentId = comment.commentId,
                                    postId = comment.postId,
                                    uid = comment.uid,
                                    nickname = user?.nickname ?: "알 수 없음",
                                    level = user?.level ?: 0,
                                    content = comment.content,
                                    createdAt = comment.createdAt
                                )
                            }
                    }.await()
                }

                _state.update {
                    it.copy(
                        commentList = updatedComments,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun addCommentToState(comment: CommentUiModel) {
        _state.update { it.copy(commentList = it.commentList + comment) }
    }

    fun removeCommentFromState(commentId: String) {
        _state.update { it.copy(commentList = it.commentList.filterNot { it.commentId == commentId }) }
    }


    fun deleteComment(postId: String, replyId: String) {
        viewModelScope.launch {
            try {
                repository.deletePostComment(postId, replyId)
                removeCommentFromState(replyId)
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
                members.forEach {
                    Log.d("todorilog", it.nickname)
                }
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun joinStudy(postId: String, study: Study) {
        val uid = auth.currentUser?.uid ?: return
        val nickname = state.value.user?.nickname ?: ""
        val member = StudyMember(
            uid = uid,
            nickname = nickname,
            studyId = study.studyId,
            role = "MEMBER",
            joinedAt = Timestamp.now()
        )

        viewModelScope.launch {
            try {
                val updatedMembers = _state.value.memberList.toMutableList()
                updatedMembers.add(member)
                _state.update { it.copy(memberList = updatedMembers) }

                repository.updateStudyMember(postId, member)
                studyRepository.addMyStudyForMember(uid, study, nickname)

                loadPostById(postId)
            } catch (e: Exception) {
                _state.update { it.copy(error = "참여 실패: ${e.message}") }
            }
        }
    }
}