package com.mukmuk.todori.data.repository

import com.mukmuk.todori.data.remote.community.StudyPost
import com.mukmuk.todori.data.remote.community.StudyPostComment
import com.mukmuk.todori.data.remote.study.Study
import com.mukmuk.todori.data.remote.study.StudyMember
import com.mukmuk.todori.data.remote.user.User
import com.mukmuk.todori.data.service.CommunityService
import javax.inject.Inject

class CommunityRepository @Inject constructor(
    private val communityService: CommunityService
){

    suspend fun createPost(post: StudyPost) {
        communityService.createPost(post)
    }

    suspend fun getPosts(filter: String? = null, data: String? = null): List<StudyPost> {
        return communityService.getPosts(filter, data)
    }

    suspend fun getPostById(postId: String): StudyPost? {
        return communityService.getPostById(postId)
    }

    suspend fun getProfile(uid: String): User? {
        return communityService.getProfile(uid)
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

    suspend fun createReply(postId: String, reply: StudyPostComment){
        communityService.createReply(postId, reply)
    }

    suspend fun getReplies(postId: String): List<StudyPostComment> {
        return communityService.getReplies(postId)
    }

    suspend fun deleteReply(postId: String, replyId: String) {
        communityService.deleteReply(postId, replyId)
    }

    suspend fun createCommentReply(postId: String, commentId: String, reply: StudyPostComment){
        communityService.createCommentReply(postId, commentId, reply)
    }

    suspend fun getCommentReplies(postId: String, commentId: String): List<StudyPostComment> {
        return communityService.getCommentReplies(postId, commentId)
    }

    suspend fun loadStudyById(studyId: String): Study? {
        return communityService.loadStudyById(studyId)
    }

    suspend fun getStudyMembers(studyId: String): List<StudyMember> {
        return communityService.getStudyMembers(studyId)
    }

    suspend fun updateStudyMember(studyId: String, member: StudyMember) {
        communityService.updateStudyMember(studyId, member)
    }

}