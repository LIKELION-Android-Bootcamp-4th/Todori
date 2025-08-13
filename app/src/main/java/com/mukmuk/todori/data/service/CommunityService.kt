package com.mukmuk.todori.data.service

import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.mukmuk.todori.data.remote.community.StudyPost
import com.mukmuk.todori.data.remote.community.StudyPostComment
import com.mukmuk.todori.data.remote.study.Study
import com.mukmuk.todori.data.remote.study.StudyMember
import com.mukmuk.todori.data.remote.user.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await


class CommunityService(
    private val firestore: FirebaseFirestore
) {
    private fun communityRef(): CollectionReference = firestore.collection("posts")


    suspend fun createPost(post: StudyPost){
        val ref = communityRef().document()
        val autoId = ref.id
        val postWithId = post.copy(postId = autoId)
        ref.set(postWithId).await()
    }

    fun getPosts(filter: String? = null, data: String? = null): Flow<List<StudyPost>> = callbackFlow {
        val query = if (filter == "참가자 수") {
            communityRef().orderBy("memberCount", Query.Direction.DESCENDING)
        } else if (filter == "날짜순") {
            communityRef().orderBy("createdAt", Query.Direction.DESCENDING)
        } else {
            communityRef()
        }

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                var posts = snapshot.documents.mapNotNull { it.toObject(StudyPost::class.java) }

                if (!data.isNullOrBlank()) {
                    posts = posts.filter {
                        it.title.contains(data) || it.content.contains(data) || it.tags.contains(
                            data
                        )
                    }
                }
                trySend(posts)
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun getPostById(postId: String): StudyPost? {
        val snapshot = communityRef().document(postId).get().await()
        return snapshot.toObject(StudyPost::class.java)
    }

    suspend fun getUserById(uid: String): User? {
        val snapshot = firestore.collection("users").document(uid).get().await()
        return snapshot.toObject(User::class.java)
    }

    suspend fun updatePost(postId: String, updatedPost: StudyPost) {
        communityRef().document(postId).set(updatedPost, SetOptions.merge()).await()
    }

    suspend fun deletePost(postId: String) {
        communityRef().document(postId).delete().await()
    }

    suspend fun createCommunitySearch(uid: String, query: String) {
        val ref = firestore.collection("users").document(uid).collection("communitySearch").document()
        val searchWithId = mapOf("query" to query, "timestamp" to FieldValue.serverTimestamp())
        ref.set(searchWithId, SetOptions.merge()).await()
    }

    suspend fun getCommunitySearch(uid: String): List<String> {
        val snapshot = firestore.collection("users")
            .document(uid)
            .collection("communitySearch")
            .limit(5)
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.get("query") as? String }.toList()
    }

    suspend fun deleteCommunitySearch(uid: String, query: String) {
        val snapshot = firestore.collection("users")
            .document(uid)
            .collection("communitySearch")
            .whereEqualTo("query", query)
            .get()
            .await()
        for (document in snapshot.documents) {
            document.reference.delete()
        }
    }

    suspend fun createPostComment(postId: String, reply: StudyPostComment){
        val ref = communityRef().document(postId).collection("studyPostReply").document()
        val autoId = ref.id
        val replyWithId = reply.copy(commentId = autoId, parentCommentId = null, createdAt = Timestamp.now())
        ref.set(replyWithId, SetOptions.merge()).await()
    }

    suspend fun getPostComments(postId: String): List<StudyPostComment> {
        val snapshot: QuerySnapshot = communityRef().document(postId).collection("studyPostReply")
            .whereEqualTo("parentCommentId", null)
            .get().await()
        return snapshot.documents.mapNotNull {
            it.toObject(StudyPostComment::class.java)?.copy(commentId = it.id) }
    }

    suspend fun deletePostComment(postId: String, replyId: String) {
        communityRef().document(postId).collection("studyPostReply").document(replyId).delete().await()
    }

    suspend fun createPostCommentReply(postId: String, commentId: String, reply: StudyPostComment){
        val ref = communityRef().document(postId).collection("studyPostReply").document()
        val autoId = ref.id
        val replyWithId = reply.copy(commentId = autoId, parentCommentId = commentId, createdAt = Timestamp.now())
        ref.set(replyWithId, SetOptions.merge()).await()
    }

    suspend fun getPostCommentReplies(postId: String, commentId: String): List<StudyPostComment> {
        val snapshot: QuerySnapshot = communityRef().document(postId).collection("studyPostReply").whereEqualTo("parentCommentId", commentId).get().await()
        return snapshot.documents.mapNotNull {
            it.toObject(StudyPostComment::class.java)?.copy(commentId = it.id)
        }
    }

    suspend fun loadStudyById(studyId: String): Study? {
        val snapshot = firestore.collection("studies").document(studyId).get().await()
        return snapshot.toObject(Study::class.java)
    }

    suspend fun getStudyMembers(studyId: String): List<StudyMember> {
        val snapshot: QuerySnapshot = firestore.collection("studyMembers").whereEqualTo("studyId", studyId).get().await()
        return snapshot.documents.mapNotNull { it.toObject(StudyMember::class.java) }
    }

    suspend fun updateStudyMember(studyId: String, member: StudyMember) {
        firestore.collection("studyMembers").document(studyId)
            .set(member, SetOptions.merge())
            .await()
    }


}