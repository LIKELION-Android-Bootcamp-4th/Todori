package com.mukmuk.todori.data.service

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.mukmuk.todori.data.remote.community.StudyPost
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

    suspend fun getPosts(filter: String? = null): List<StudyPost> {
        val snapshot: QuerySnapshot = if (filter == "맴버 수") {
            communityRef().orderBy("memberCount").get().await()
        }
        else if(filter == "날짜순"){
            communityRef().orderBy("createdAt", Query.Direction.DESCENDING).get().await()
        }
        else {
            communityRef().get().await()
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
}