package com.mukmuk.todori.util

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import android.os.Build
import androidx.annotation.RequiresApi

object WidgetUtil {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun loadWidgetTodos(uid: String): List<Pair<String, Boolean>> {
        val today = LocalDate.now()
        val todayStr = today.toString()
        val snapshot = FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("todos")
            .whereEqualTo("date", todayStr)
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            val title = doc.getString("title") ?: return@mapNotNull null
            val completed = doc.getBoolean("completed") ?: false
            title to completed
        }
    }
}