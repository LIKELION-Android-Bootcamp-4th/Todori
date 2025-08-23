package com.mukmuk.todori.data.service

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
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
    suspend fun createCategory(uid: String, category: TodoCategory): String {
        val ref = userCategoriesRef(uid).document() // auto-ID 문서
        val autoId = ref.id
        val categoryWithId = category.copy(categoryId = autoId)
        ref.set(categoryWithId, SetOptions.merge()).await()

        return autoId
    }

    suspend fun createSendTodoCategory(uid: List<String> = emptyList(), category: TodoCategory): String{
        val ref = firestore.collection("sendTodoCategories").document()

        if(ref.get().await().exists()){
            val todoCategoryRef = firestore.collection("sendTodoCategories")
            val snapshot = todoCategoryRef
                .whereArrayContains("category", category)
                .get()
                .await()

            if(snapshot.documents.isNotEmpty()){
                val sendCategoryId = snapshot.documents.first().id

                return sendCategoryId
            }
        }

        val autoId = ref.id

        val data = mapOf(
            "sendCategoryId" to autoId,
            "users" to uid,
            "category" to category
        )

        ref.set(data, SetOptions.merge()).await()
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

    suspend fun getSendCategory(uid: String, sendCategoryId: String): TodoCategory? {
        val todoCategoryRef = firestore.collection("sendTodoCategories")
        val snapshot = todoCategoryRef
            .whereEqualTo("sendCategoryId", sendCategoryId)
            .get()
            .await()

        val users = snapshot.documents.firstOrNull()?.get("users") as? List<String>

        val category = snapshot.documents.firstOrNull()?.toObject(TodoCategory::class.java)


        if (users?.contains(uid) != true) {
            todoCategoryRef.document(sendCategoryId)
                .update("users", FieldValue.arrayUnion(uid))
                .await()
        }

        return category
    }

    suspend fun getSendCategories(uid: String): List<TodoCategory>  {
        val todoCategoryRef = firestore.collection("sendTodoCategories")
        val snapshot = todoCategoryRef
            .whereArrayContains("users", uid)
            .get()
            .await()

        val categories = snapshot.documents.mapNotNull { it.get("category") as? TodoCategory }



        return categories
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
