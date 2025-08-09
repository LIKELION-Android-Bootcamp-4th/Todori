package com.mukmuk.todori.ui.screen.community

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mukmuk.todori.data.remote.community.StudyPost
import com.mukmuk.todori.data.repository.CommunityRepository
import com.mukmuk.todori.data.repository.StudyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val communityRepository: CommunityRepository,
    private val studyRepository: StudyRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CommunityState())
    val state: StateFlow<CommunityState> = _state.asStateFlow()

    var memberCount = 0


    fun loadPosts(filter: String? = null) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val posts = communityRepository.getPosts(filter = filter)
                _state.update {
                    it.copy(
                        allPostList = if(filter.isNullOrBlank()) posts else it.allPostList,
                        postList = posts,
                        isLoading = false,
                        error = null
                    )
                }

                val updatedPosts = coroutineScope {
                    posts.map { post ->
                        async {
                            if (post.studyId.isNotBlank()) {
                                val members = studyRepository.getMembersForStudies(listOf(post.studyId))
                                post.copy(memberCount = members.size)
                            } else {
                                post
                            }
                        }
                    }.awaitAll()
                }

                _state.update {
                    it.copy(postList = updatedPosts)
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
                val posts = communityRepository.getPosts(data = data)
                _state.update {
                    it.copy(
                        communitySearchPostList = posts,
                        isLoading = false,
                        error = null,
                    )
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


    fun createPost(post: StudyPost) {
        viewModelScope.launch {
            try {
                communityRepository.createPost(post)
                _state.update {
                    it.copy(postList = it.postList + post, error = null)
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }


    fun updatePost(postId: String, updatedPost: StudyPost) {
        viewModelScope.launch {
            try {
                communityRepository.updatePost(postId, updatedPost)
                _state.update {
                    it.copy(
                        postList = it.postList.map { post ->
                            if (post.postId == postId) updatedPost else post
                        }
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
                communityRepository.deletePost(postId)
                _state.update {
                    it.copy(postList = it.postList.filter { post ->
                        post.postId != postId
                    })
                }
            }
            catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun createCommunitySearch(uid: String, query: String) {
        viewModelScope.launch {
            try {
                communityRepository.createCommunitySearch(uid, query)
                _state.update {
                    it.copy(error = null)
                }
            }
            catch (e: Exception) {
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