package com.mukmuk.todori.util

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.datastore.preferences.core.stringPreferencesKey

object WidgetUtil {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun loadWidgetTodos(context: Context): List<Pair<String, Boolean>> {
        val prefs = context.dataStore.data.first()
        val today = LocalDate.now().toString()

        val todoJson = prefs[stringPreferencesKey("$today")] ?: "[]"

        val todos = Json.decodeFromString<List<TodoItem>>(todoJson)
        return todos.map { it.task to it.completed }
    }
}