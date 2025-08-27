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

                    val updatedPosts = coroutineScope {
                        _state.update { it.copy(isLoading = true) }
                        posts.map { post ->
                            async {
                                val membersCount =
                                    communityRepository.getStudyMembers(post.studyId).size
                                if (post.memberCount != membersCount) {
                                    val updatedPost = post.copy(memberCount = membersCount)
                                    communityRepository.updatePost(post.postId, updatedPost)
                                    updatedPost
                                } else {
                                    post
                                }
                            }
                        }.awaitAll()
                    }

                    _state.update {
                        it.copy(
                            allPostList = updatedPosts,
                            postList = updatedPosts,
                            isLoading = false,
                        )
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
            }
            catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun setData(category: String) {
        val allPosts = state.value.allPostList

        val filteredPosts = when(category) {
            "전체" -> allPosts
            else -> {
                val matchingTags = StudyCategory.entries
                    .find { it.displayName == category }?.tags ?: listOf(category)

                allPosts.filter { post ->
                    post.tags.any { it in matchingTags }
                }
            }
        }

        _state.update { it.copy(postList = filteredPosts) }
    }

    fun setSelectedData(data: String){
        _state.update {
            it.copy(selectedOption = data)
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