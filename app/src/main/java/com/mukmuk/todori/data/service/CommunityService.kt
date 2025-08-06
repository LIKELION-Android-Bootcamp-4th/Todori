package com.mukmuk.todori.data.service

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
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

    suspend fun getPosts(): List<StudyPost> {
        val snapshot = communityRef().get().await()
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