package com.mukmuk.todori.util

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object WidgetUtil {
    suspend fun loadWidgetTodos(uid: String): List<Pair<String, Boolean>> {
        val snapshot = FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("todos")
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            val title = doc.getString("title") ?: return@mapNotNull null
            val done = doc.getBoolean("done") ?: false
            title to done
        }
    }
}