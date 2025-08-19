package com.mukmuk.todori.data.service

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.mukmuk.todori.data.remote.todo.TodoCategory
import kotlinx.coroutines.tasks.await

class TodoCategoryService(
    private val firestore: FirebaseFirestore
) {
    // 유저별 카테고리 컬렉션 경로
    private fun userCategoriesRef(uid: String): CollectionReference =
        firestore.collection("users").document(uid).collection("todoCategories")

    // 카테고리 생성
    suspend fun createCategory(uid: String, category: TodoCategory) {
        val ref = userCategoriesRef(uid).document() // auto-ID 문서
        val autoId = ref.id
        val categoryWithId = category.copy(categoryId = autoId)
        ref.set(categoryWithId, SetOptions.merge()).await()
    }

    suspend fun createSendTodoCategory(category: TodoCategory): String{
        val ref = firestore.collection("sendTodoCategories").document()
        val autoId = ref.id
        val categoryWithId = category.copy(categoryId = autoId)
        ref.set(categoryWithId, SetOptions.merge()).await()
        return autoId
    }

    suspend fun getCategoryById(uid: String, categoryId: String): TodoCategory? {
        val snapshot = firestore.collection("users")
            .document(uid)
            .collection("todoCategories")
            .document(categoryId)
            .get()
            .await()
        return snapshot.toObject(TodoCategory::class.java)
    }

    // 카테고리 목록 조회
    suspend fun getCategories(uid: String): List<TodoCategory> {
        val snapshot: QuerySnapshot = userCategoriesRef(uid).get().await()
        return snapshot.documents.mapNotNull { it.toObject(TodoCategory::class.java) }
    }

    suspend fun getSendCategory(categoryId: String): TodoCategory? {
        val snapshot = firestore.collection("sendTodoCategories")
            .document(categoryId)
            .get()
            .await()
        return snapshot.toObject(TodoCategory::class.java)
    }

    // 카테고리 수정
    suspend fun updateCategory(uid: String, category: TodoCategory) {
        userCategoriesRef(uid)
            .document(category.categoryId)
            .set(category, SetOptions.merge())
            .await()
    }

    // 카테고리 삭제
    suspend fun deleteCategory(uid: String, categoryId: String) {
        userCategoriesRef(uid).document(categoryId).delete().await()
    }
}
