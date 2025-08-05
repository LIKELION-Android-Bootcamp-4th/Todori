package com.mukmuk.todori.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.mukmuk.todori.data.remote.todo.Todo
import com.mukmuk.todori.data.service.TodoService
import java.time.LocalDate
import javax.inject.Inject

class TodoRepository @Inject constructor(
    private val todoService: TodoService
) {
    suspend fun createTodo(uid: String, todo: Todo) = todoService.createTodo(uid, todo)
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getTodosByDate(uid: String, date: LocalDate) = todoService.getTodosByDate(uid, date)
    suspend fun getAllTodos(uid: String) = todoService.getAllTodos(uid)
    suspend fun updateTodo(uid: String, todo: Todo) = todoService.updateTodo(uid, todo)
    suspend fun deleteTodo(uid: String, todoId: String) = todoService.deleteTodo(uid, todoId)
}
