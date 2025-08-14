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
//object WidgetUtil {
//    fun saveWidgetTodos(context: Context, todos: List<Pair<String, Boolean>>) {
//        val todosMap = todos.map { (title, completed) ->
//            mapOf("title" to title, "completed" to completed)
//        }
//        FirebaseFirestore.getInstance()
//            .collection("users")
//            .document("uid")
//            .collection("todos")
//            .document("data")
//            .set(mapOf("todos" to todosMap))
//    }
//    suspend fun loadWidgetTodos(uid: String): List<Pair<String, Boolean>> {
//        val snapshot = FirebaseFirestore.getInstance()
//            .collection("users")
//            .document(uid)
//            .collection("todos")
//            .document("data")
//            .get()
//            .await()
//
//        val todos = snapshot.get("todos") as? List<Map<String, Any>> ?: return emptyList()
//        return todos.map {
//            val title = it["title"] as? String ?: ""
//            val done = it["done"] as? Boolean ?: false
//            title to done
//        }
//    }
//}