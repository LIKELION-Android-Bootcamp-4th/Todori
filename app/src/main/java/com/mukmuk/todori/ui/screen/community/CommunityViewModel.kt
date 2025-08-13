package com.mukmuk.todori.ui.screen.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mukmuk.todori.data.remote.community.StudyPost
import com.mukmuk.todori.data.remote.community.StudyPostComment
import com.mukmuk.todori.data.remote.study.StudyMember
import com.mukmuk.todori.data.repository.CommunityRepository
import com.mukmuk.todori.data.repository.StudyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val communityRepository: CommunityRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CommunityState())
    val state: StateFlow<CommunityState> = _state.asStateFlow()

    fun loadPosts(filter: String? = _state.value.selectedOption) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                if(filter == "참가자 수")
                    _state.update { it.copy(selectedOption = "참가자 수") }
                else if(filter == "날짜순")
                    _state.update { it.copy(selectedOption = "날짜순") }

                communityRepository.getPosts(filter = filter).collect { posts ->
                    _state.update {
                        it.copy(
                            allPostList = posts,
                            postList = posts,
                            isLoading = false,
                        )
                    }
                }

                _state.value.postList.map {
                    if(it.studyId.isNotBlank()) {
                        val memberCount = communityRepository.getStudyMembers(it.studyId).size
                        it.copy(memberCount = memberCount)
                    }

                    if(it.commentsCount > 0){
                        val comments = communityRepository.getPostComments(it.postId)
                        val allReplies = comments.flatMap { comment ->
                            communityRepository.getPostCommentReplies(it.postId, comment.commentId)
                        }
                        val commentsCount = comments.size + allReplies.size
                        it.copy(commentsCount = commentsCount)
                    }
                }


            }
            catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun loadSearchPosts(data: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                communityRepository.getPosts(data = data).collect { posts ->
                    _state.update {
                        it.copy(
                            communitySearchPostList = posts,
                            isLoading = false,
                        )
                    }
                }

                _state.value.communitySearchPostList.map {
                    if(it.studyId.isNotBlank()) {
                        val memberCount = communityRepository.getStudyMembers(it.studyId).size
                        it.copy(memberCount = memberCount)
                    }
                }
            }
            catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun setData(data: String){
        _state.update {
            it.copy(postList = state.value.allPostList)
        }
        if(data == "전체") {
            _state.update {
                it.copy(postList = state.value.allPostList)
            }
        }
        else {
            _state.update {
                it.copy(postList = state.value.allPostList.filter { post ->
                    post.tags.contains(data)
                })
            }
        }
    }

    fun getAllCommentCount(postId: String) {
        viewModelScope.launch {
            try {

                val comments = communityRepository.getPostComments(postId)

                val allReplies = comments.flatMap { comment ->
                    communityRepository.getPostCommentReplies(postId, comment.commentId)
                }


                _state.update {
                    it.copy(
                        commentList = it.commentList + mapOf(postId to comments),
                        commentReplyList = it.commentReplyList + mapOf(postId to allReplies)
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun createCommunitySearch(uid: String, query: String) {
        viewModelScope.launch {
            try {
                communityRepository.createCommunitySearch(uid, query)
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun deleteCommunitySearch(uid: String, query: String) {
        viewModelScope.launch {
            try {
                communityRepository.deleteCommunitySearch(uid, query)
                getCommunitySearch(uid)
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun getCommunitySearch(uid: String) {
        viewModelScope.launch {
            try {
                val searches = communityRepository.getCommunitySearch(uid)
                _state.update {
                    it.copy(communitySearchList = searches)
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

}