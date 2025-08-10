package com.mukmuk.todori.ui.screen.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mukmuk.todori.data.remote.community.StudyPost
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

    fun createCommunitySearch(uid: String, query: String) {
        viewModelScope.launch {
            try {
                if(state.value.communitySearchList.find { it == query } != null) return@launch
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