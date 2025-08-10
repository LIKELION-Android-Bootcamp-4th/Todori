package com.mukmuk.todori.data.service

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.mukmuk.todori.data.remote.community.StudyPost
import com.mukmuk.todori.data.remote.community.StudyPostComment
import com.mukmuk.todori.data.remote.study.StudyMember
import kotlinx.coroutines.tasks.await


class CommunityService(
    private val firestore: FirebaseFirestore
) {
    private fun communityRef(): CollectionReference = firestore.collection("posts")
    private fun replyRef(): CollectionReference = firestore.collection("studyPostReply")


    suspend fun createPost(post: StudyPost){
        val ref = communityRef().document()
        val autoId = ref.id
        val postWithId = post.copy(postId = autoId)
        ref.set(postWithId).await()
    }

    suspend fun getPosts(filter: String? = null, data: String? = null): List<StudyPost> {
        val snapshot: QuerySnapshot = if (filter == "맴버 수") {
            communityRef().orderBy("memberCount").get().await()
        }
        else if(filter == "날짜순"){
            communityRef().orderBy("createdAt", Query.Direction.DESCENDING).get().await()
        }
        else {
            communityRef().get().await()
        }
        if(data != null){
            return snapshot.documents.mapNotNull { it.toObject(StudyPost::class.java) }.filter { it.title.contains(data) || it.content.contains(data) || it.tags.contains(data) }
        }
        return snapshot.documents.mapNotNull { it.toObject(StudyPost::class.java) }
    }

    suspend fun getPostById(postId: String): StudyPost? {
        val snapshot = communityRef().document(postId).get().await()
        return snapshot.toObject(StudyPost::class.java)
    }

    suspend fun updatePost(postId: String, updatedPost: StudyPost) {
        communityRef().document(postId).set(updatedPost).await()
    }

    suspend fun deletePost(postId: String) {
        communityRef().document(postId).delete().await()
    }

    suspend fun createCommunitySearch(uid: String, query: String) {
        val ref = firestore.collection("users")
            .document(uid)
            .collection("communitySearch")
            .document()

        val data = mapOf("query" to query, "timestamp" to FieldValue.serverTimestamp())


        ref.set(data).await()
    }

    suspend fun getCommunitySearch(uid: String): List<String> {
        val snapshot = firestore.collection("users")
            .document(uid)
            .collection("communitySearch")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .await()

        return snapshot.documents.mapNotNull { it.getString("query") }
    }

    suspend fun createReply(postId: String, reply: StudyPostComment){
        val ref = communityRef().document(postId).collection("studyPostReply").document()
        val autoId = ref.id
        val replyWithId = reply.copy(commentId = autoId)
        ref.set(replyWithId).await()
    }

    suspend fun getReplies(postId: String): List<StudyPostComment> {
        val snapshot: QuerySnapshot = communityRef().document(postId).collection("studyPostReply").get().await()
        return snapshot.documents.mapNotNull { it.toObject(StudyPostComment::class.java) }
    }

    suspend fun deleteReply(postId: String, replyId: String) {
        communityRef().document(postId).collection("studyPostReply").document(replyId).delete().await()
    }

    suspend fun createCommentReply(postId: String, commentId: String, reply: StudyPostComment){
        val ref = communityRef().document(postId).collection("studyPostReply").document()
        val autoId = ref.id
        val replyWithId = reply.copy(parentCommentId = commentId, commentId = autoId)
        ref.set(replyWithId).await()
    }

    suspend fun getCommentReplies(postId: String, commentId: String): List<StudyPostComment> {
        val snapshot: QuerySnapshot = communityRef().document(postId).collection("studyPostReply").whereEqualTo("parentCommentId", commentId).get().await()
        return snapshot.documents.mapNotNull { it.toObject(StudyPostComment::class.java) }
    }

    suspend fun updateStudyMember(studyId: String, member: StudyMember) {
        val docId = member.uid
        firestore.collection("studyMembers").document(docId)
            .set(member, SetOptions.merge())
            .await()
    }


}