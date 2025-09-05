package com.mukmuk.todori.data.service

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.mukmuk.todori.data.remote.todo.SendCategory
import com.mukmuk.todori.data.remote.todo.TodoCategory
import com.mukmuk.todori.data.remote.user.User
import kotlinx.coroutines.tasks.await

class TodoCategoryService(
    private val firestore: FirebaseFirestore
) {
    private fun userCategoriesRef(uid: String): CollectionReference =
        firestore.collection("users").document(uid).collection("todoCategories")

    suspend fun getUserById(uid: String): User? {
        val snapshot = firestore.collection("users").document(uid).get().await()
        return snapshot.toObject(User::class.java)
    }

    suspend fun createCategory(uid: String, category: TodoCategory): String {
        val ref = userCategoriesRef(uid).document()
        val autoId = ref.id
        val categoryWithId = category.copy(categoryId = autoId)
        ref.set(categoryWithId, SetOptions.merge()).await()

        return autoId
    }

    suspend fun createSendTodoCategory(uid: List<String> = emptyList(), categoryId: String): String {
        val todoCategoryRef = firestore.collection("sendTodoCategories")
        val snapshot = todoCategoryRef
            .whereEqualTo("categoryId", categoryId)
            .get()
            .await()

        if (snapshot.documents.isNotEmpty()) {
            return snapshot.documents.first().id
        }


        val ref = todoCategoryRef.document()
        val autoId = ref.id

        val sendCategory = SendCategory(
            sendCategoryId = autoId,
            users = uid,
            categoryId = categoryId
        )

        ref.set(sendCategory, SetOptions.merge()).await()
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

    suspend fun getCategoryByData(categoryId: String): TodoCategory? {
        val snapshot = FirebaseFirestore.getInstance()
            .collectionGroup("todoCategories")
            .whereEqualTo("categoryId", categoryId)
            .get()
            .await()

        return snapshot.documents.firstOrNull()?.toObject(TodoCategory::class.java)
    }

    suspend fun getCategories(uid: String): List<TodoCategory> {
        val snapshot: QuerySnapshot = userCategoriesRef(uid).get().await()
        return snapshot.documents.mapNotNull { it.toObject(TodoCategory::class.java) }
    }

    suspend fun getSendCategory(uid: String, sendCategoryId: String): TodoCategory? {
        val ref = firestore.collection("sendTodoCategories")
        val snapshot = ref
            .document(sendCategoryId)
            .get()
            .await()

        val sendCategory = snapshot.toObject(SendCategory::class.java) ?: return null

        if (!sendCategory.users.contains(uid)) {
            ref.document(sendCategoryId)
                .update("users", FieldValue.arrayUnion(uid))
                .await()
        }

        return getCategoryByData(sendCategory.categoryId)
    }

    suspend fun getSendCategories(uid: String): List<TodoCategory>  {
        val todoCategoryRef = firestore.collection("sendTodoCategories")
        val snapshot = todoCategoryRef
            .whereArrayContains("users", uid)
            .get()
            .await()

        val categories = mutableListOf<TodoCategory>()

        for(data in snapshot.documents){
            val categoryId = data.getString("categoryId") ?: continue

            val snapshot = FirebaseFirestore.getInstance()
                .collectionGroup("todoCategories")
                .whereEqualTo("categoryId", categoryId)
                .get()
                .await()

            snapshot.documents.firstOrNull()?.toObject(TodoCategory::class.java)
                ?.let { categories.add(it) }
        }



        return categories
    }

    suspend fun deleteSendCategory(uid: String, categoryId: String) {
        val snapshot = firestore.collection("sendTodoCategories")
            .whereEqualTo("categoryId", categoryId)
            .get()
            .await()

        for (doc in snapshot.documents) {
            doc.reference.update("users", FieldValue.arrayRemove(uid)).await()
        }
    }

    suspend fun updateCategory(uid: String, category: TodoCategory) {
        userCategoriesRef(uid)
            .document(category.categoryId)
            .set(category, SetOptions.merge())
            .await()
    }


    suspend fun deleteCategory(uid: String, categoryId: String) {
        userCategoriesRef(uid).document(categoryId).delete().await()
        val todoCategoryRef = firestore.collection("sendTodoCategories")
        val snapshot = todoCategoryRef
            .whereEqualTo("categoryId", categoryId)
            .get()
            .await()
        for (data in snapshot.documents) {
            data.reference.delete()
        }
    }
}
