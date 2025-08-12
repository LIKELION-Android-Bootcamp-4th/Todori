package com.mukmuk.todori.data.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.mukmuk.todori.data.remote.todo.Todo
import com.mukmuk.todori.data.service.TodoService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.time.LocalDate
import javax.inject.Inject

class TodoRepository @Inject constructor(
    private val todoService: TodoService,
) {
    suspend fun createTodo(uid: String, todo: Todo) = todoService.createTodo(uid, todo)
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getTodosByDate(uid: String, date: LocalDate) = todoService.getTodosByDate(uid, date)
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getTodosByWeek(uid: String, sunday: LocalDate, saturday: LocalDate) = todoService.getTodosByWeek(uid, sunday, saturday)

    suspend fun getTodosByCategoryAndDate(uid: String, categoryId: String, date: String): List<Todo> {
        return todoService.getTodosByCategoryAndDate(uid, categoryId, date)
    }

    suspend fun getTodosByCategory(uid: String, categoryId: String): List<Todo> =
        todoService.getTodosByCategory(uid, categoryId)

    suspend fun updateTodo(uid: String, todo: Todo) = todoService.updateTodo(uid, todo)
    suspend fun deleteTodo(uid: String, todoId: String) = todoService.deleteTodo(uid, todoId)

    fun observeTodos(uid: String, onTodosChanged: (List<Todo>) -> Unit) {
        todoService.getTodosCollection(uid)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("TodoRepository", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val todos = snapshot.documents.mapNotNull { it.toObject(Todo::class.java) }
                    onTodosChanged(todos)
                } else {
                    onTodosChanged(emptyList())
                }
            }
    }
}
