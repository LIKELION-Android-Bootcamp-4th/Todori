package com.mukmuk.todori.data.service

import android.util.Log
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
        Log.d("TodoCategoryService", "Firestore set() 직전: $categoryWithId")
        try {
            Log.d("TodoCategoryService", "Firestore try t시작")

            ref.set(categoryWithId, SetOptions.merge()).await()
        } catch (e: Exception) {
            Log.e("TodoCategoryService", "Firestore set() 중 오류!", e)
        }
        Log.d("TodoCategoryService", "Firestore set() 완료")
    }

    // 카테고리 목록 조회
    suspend fun getCategories(uid: String): List<TodoCategory> {
        val snapshot: QuerySnapshot = userCategoriesRef(uid).get().await()
        return snapshot.documents.mapNotNull { it.toObject(TodoCategory::class.java) }
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
