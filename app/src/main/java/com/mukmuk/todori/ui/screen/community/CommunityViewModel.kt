package com.mukmuk.todori.ui.screen.community

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.mukmuk.todori.ui.screen.community.components.CommentList
import com.mukmuk.todori.ui.screen.community.components.StudyPost


class CommunityViewModel: ViewModel() {
    val postList = mutableStateListOf<StudyPost>()

    var selectedPost: StudyPost? by mutableStateOf(null)

    var commentList = mutableStateListOf<CommentList>()

    var menu = listOf("수정", "삭제")

    var td = listOf("답글 달기", "삭제")



    var data = 1

    fun addPost(title: String, content: String) {
        postList.add(StudyPost(title = title, content = content))
    }

    fun updatePost(post: StudyPost) {
        data = 2
        val index = postList.indexOf(post)
        if (index != -1) {
            postList[index] = post
        }
    }

    fun deletePost(post: StudyPost) {
        postList.remove(post)
    }

    fun deleteComment(comment: CommentList) {
        commentList.remove(comment)
    }

}