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
import kotlinx.coroutines.launch

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val repository: CommunityRepository
) : ViewModel() {
    val postList = mutableStateListOf<StudyPost>()

    var selectedPost by mutableStateOf<StudyPost?>(null)

    var commentList = null

    var menu = listOf("수정", "삭제")

    var td = listOf("답글 달기", "삭제")

    var data = 1

    var isLoading by mutableStateOf(false)


    fun loadPosts() {
        viewModelScope.launch {
            val posts = repository.getPosts()
            postList.clear()
            postList.addAll(posts)
        }
    }

    fun loadPostById(postId: String) {
        viewModelScope.launch {
            val post = repository.getPostById(postId)
            selectedPost = post
        }
    }

    fun createPost(post: StudyPost) {
        viewModelScope.launch {
            repository.createPost(post)
            postList.add(post)
        }
    }

    fun updatePost(post: StudyPost) {
        data = 2
        val index = postList.indexOf(post)
        if (index != -1) {
            postList[index] = post
        }
    }

    fun deletePost(post: StudyPost) {
        viewModelScope.launch {
            repository.deletePost(post.postId)
            postList.remove(post)
        }
    }



}