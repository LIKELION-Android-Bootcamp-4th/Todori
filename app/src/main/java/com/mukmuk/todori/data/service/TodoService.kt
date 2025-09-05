package com.mukmuk.todori.data.service

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.mukmuk.todori.data.remote.todo.Todo
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TodoService(
    private val firestore: FirebaseFirestore
) {
    private fun userTodosRef(uid: String) =
        firestore.collection("users").document(uid).collection("todos")

    suspend fun createTodo(uid: String, todo: Todo) {
        val ref = userTodosRef(uid).document()
        val todoWithId = todo.copy(todoId = ref.id)
        ref.set(todoWithId).await()
    }

    suspend fun getTodosByCategory(uid: String, categoryId: String): List<Todo> {
        val snapshot = userTodosRef(uid)
            .whereEqualTo("categoryId", categoryId)
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.toObject(Todo::class.java) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getTodosByDate(uid: String, date: LocalDate): List<Todo> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = date.format(formatter)
        val snapshot: QuerySnapshot = userTodosRef(uid)
            .whereEqualTo("date", formattedDate)
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.toObject(Todo::class.java) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getTodosByWeek(uid: String, sunday: LocalDate, saturday: LocalDate): List<Todo> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val startDate = sunday.format(formatter)
        val endDate = saturday.format(formatter)

        val snapshot: QuerySnapshot = userTodosRef(uid)
            .whereGreaterThanOrEqualTo("date", startDate)
            .whereLessThanOrEqualTo("date", endDate)
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.toObject(Todo::class.java) }
    }

    suspend fun getTodosByCategoryAndDate(
        uid: String,
        categoryId: String,
        date: String
    ): List<Todo> {
        val snapshot = userTodosRef(uid)
            .whereEqualTo("categoryId", categoryId)
            .whereEqualTo("date", date)
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.toObject(Todo::class.java) }
    }

    suspend fun updateTodo(uid: String, todo: Todo) {
        userTodosRef(uid).document(todo.todoId)
            .set(todo, SetOptions.merge())
            .await()
    }

    suspend fun deleteTodo(uid: String, todoId: String) {
        userTodosRef(uid).document(todoId).delete().await()
    }

    fun getTodosCollection(uid: String) =
        firestore.collection("users")
            .document(uid)
            .collection("todos")
}
