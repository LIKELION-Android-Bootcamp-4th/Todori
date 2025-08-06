package com.mukmuk.todori.data.repository

import com.mukmuk.todori.data.remote.community.StudyPost
import com.mukmuk.todori.data.service.CommunityService
import jakarta.inject.Inject

class CommunityRepository @Inject constructor(
    private val communityService: CommunityService
){

    suspend fun createPost(post: StudyPost) {
        communityService.createPost(post)
    }

    suspend fun getPosts(): List<StudyPost> {
        return communityService.getPosts()
    }

    suspend fun getPostById(postId: String): StudyPost? {
        return communityService.getPostById(postId)
    }

    suspend fun updatePost(postId: String, updatedPost: StudyPost) {
        communityService.updatePost(postId, updatedPost)
    }

    suspend fun deletePost(postId: String) {
        communityService.deletePost(postId)
    }
}