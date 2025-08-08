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

    var commentList = null


    fun loadPosts(filter: String? = null, data: String? = null) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val posts = repository.getPosts(filter, data)
                _state.update {
                    it.copy(
                        allPostList = if(filter == null && data == null) posts else it.allPostList,
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
                repository.createPost(post)
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
                repository.updatePost(postId, updatedPost)
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
                repository.deletePost(postId)
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
                repository.createCommunitySearch(uid, query)
            }
            catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun getCommunitySearch(uid: String) {
        viewModelScope.launch {
            try {
                val searches = repository.getCommunitySearch(uid)
                _state.update {
                    it.copy(communitySearchList = searches)
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

}