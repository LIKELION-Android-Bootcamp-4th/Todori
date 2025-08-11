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
) : ViewModel() {

    private val _state = MutableStateFlow(CommunityState())
    val state: StateFlow<CommunityState> = _state.asStateFlow()

    fun loadPosts(filter: String? = _state.value.selectedOption) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                if(filter == "맴버 수")
                    _state.update { it.copy(selectedOption = "참가자 수") }
                else if(filter == "날짜순")
                    _state.update { it.copy(selectedOption = "날짜순") }
                val posts = communityRepository.getPosts(filter = filter)
                val memberList: MutableMap<String, Int> = mutableMapOf()
                posts.map {
                    if(it.studyId.isNotBlank()) {
                        val memberCount = communityRepository.getStudyMembers(it.studyId)
                        memberList[it.postId] = memberCount.size
                    }
                }
                _state.update {
                    it.copy(
                        allPostList = posts,
                        postList = posts,
                        memberList = memberList,
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

    fun loadSearchPosts(data: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val posts = communityRepository.getPosts(data = data)
                val memberList: MutableMap<String, Int> = mutableMapOf()
                posts.map {
                    if(it.studyId.isNotBlank()) {
                        val memberCount = communityRepository.getStudyMembers(it.studyId)
                        memberList[it.postId] = memberCount.size
                    }
                }
                _state.update {
                    it.copy(
                        communitySearchPostList = posts,
                        memberList = memberList,
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