package com.mukmuk.todori.data.repository

import com.mukmuk.todori.data.remote.community.StudyPost
import com.mukmuk.todori.data.remote.community.StudyPostComment
import com.mukmuk.todori.data.remote.study.Study
import com.mukmuk.todori.data.remote.study.StudyMember
import com.mukmuk.todori.data.remote.user.User
import com.mukmuk.todori.data.service.CommunityService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CommunityRepository @Inject constructor(
    private val communityService: CommunityService
) {

    suspend fun createPost(post: StudyPost) {
        communityService.createPost(post)
    }

    fun getPosts(filter: String? = null, data: String? = null): Flow<List<StudyPost>> {
        return communityService.getPosts(filter, data)
    }

    fun getPostById(postId: String): Flow<StudyPost?> {
        return communityService.getPostById(postId)
    }

    suspend fun getUserById(uid: String): User? {
        return communityService.getUserById(uid)
    }

    suspend fun updatePost(postId: String, updatedPost: StudyPost) {
        communityService.updatePost(postId, updatedPost)
    }

    suspend fun deletePost(postId: String) {
        communityService.deletePost(postId)
    }

    suspend fun createCommunitySearch(uid: String, query: String) {
        communityService.createCommunitySearch(uid, query)
    }

    suspend fun getCommunitySearch(uid: String): List<String> {
        return communityService.getCommunitySearch(uid)
    }

    suspend fun deleteCommunitySearch(uid: String, query: String) {
        communityService.deleteCommunitySearch(uid, query)
    }

    suspend fun createPostComment(postId: String, reply: StudyPostComment): StudyPostComment {
        return communityService.createPostComment(postId, reply)
    }

    suspend fun getPostComments(postId: String): List<StudyPostComment> {
        return communityService.getPostComments(postId)
    }

    suspend fun deletePostComment(postId: String, replyId: String) {
        communityService.deletePostComment(postId, replyId)
    }

    suspend fun createPostCommentReply(postId: String, commentId: String, reply: StudyPostComment) {
        communityService.createPostCommentReply(postId, commentId, reply)
    }

    suspend fun getPostCommentReplies(postId: String, commentId: String): List<StudyPostComment> {
        return communityService.getPostCommentReplies(postId, commentId)
    }

    fun loadStudyById(studyId: String): Flow<Study?> {
        return communityService.loadStudyById(studyId)
    }

    suspend fun getStudyMembers(studyId: String): List<StudyMember> {
        return communityService.getStudyMembers(studyId)
    }

    suspend fun updateStudyMember(postId: String, member: StudyMember) {
        communityService.updateStudyMember(postId, member)
    }

}